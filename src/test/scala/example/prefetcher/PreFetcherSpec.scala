package example.prefetcher

import zio.ZIO
import zio.logging.Logging
import zio.test.DefaultRunnableSpec
import zio.test._
import zio.test.Assertion.equalTo
import zio.test.environment.TestClock
import zio.duration._

object PreFetcherSpec extends DefaultRunnableSpec {

  val logEnv = Logging.console(
    format = (_, logEntry) => logEntry,
    rootLoggerName = Some("test-logger")
  )

  override def spec = suite("PrefetchingSpec") (
    testM("The dirty counter incrementing effect work as expected") {
      val incr = new Incr().supplier
      for {
        v1 <- incr
        v2 <- incr
      } yield assert(v1)(equalTo(0)) && assert(v2)(equalTo(1))
    },

    testM("Clean Incrementer effect Should work as expected") (
      for {
        v1 <- incrementer.provide(-1)
        v2 <- incrementer.provide(v1)
      } yield assert(v1)(equalTo(0)) && assert(v2)(equalTo(1))
    ),

    testM("Correctly update the pre-fetched ref")(
      for {
        prefetcher <-
          Prefetcher.withInitialValue(0, incrementer, 1.second, 100.millis).provideCustomLayer(logEnv)
        immediatelyHeld     <- prefetcher.currentValueRef.get
        _                   <- TestClock.adjust(100.millis)
        initialSupplierCall <- prefetcher.currentValueRef.get
        _                   <- TestClock.adjust(1.second)
        secondSupplierCall  <- prefetcher.currentValueRef.get
      } yield assert(immediatelyHeld)(equalTo(0)) &&
        assert(initialSupplierCall)(equalTo(1)) &&
        assert(secondSupplierCall)(equalTo(2))
    ),

    testM("Correctly handle the errors")(
      for {
        prefetcher  <-
          Prefetcher.withInitialValue(-42, new FailingCounter().supplier, 1.second).provideCustomLayer(logEnv)
        immediatelyHeld <- prefetcher.currentValueRef.get
        _ <- TestClock.adjust(100.millis)
        initialSupplierCall <- prefetcher.currentValueRef.get
        _ <- TestClock.adjust(1.second)
        secondSupplierCall <- prefetcher.currentValueRef.get
      } yield assert(immediatelyHeld)(equalTo(-42)) &&
        assert(initialSupplierCall)(equalTo(-42)) &&
        assert(secondSupplierCall)(equalTo(1))
    ),

    testM("Correctly work with a supplier that ignores the previous value") {
      val incr = new Incr().supplier
      for {
        prefetcher          <- Prefetcher.withInitialValue(-42, incr, 1.second, 100.millis).provideCustomLayer(logEnv)
        immediatelyHeld     <- prefetcher.currentValueRef.get
        _                   <- TestClock.adjust(100.millis)
        initialSupplierCall <- prefetcher.currentValueRef.get
        _                   <- TestClock.adjust(1.second)
        secondSupplierCall  <- prefetcher.currentValueRef.get
      } yield assert(immediatelyHeld)(equalTo(-42)) &&
        assert(initialSupplierCall)(equalTo(0)) &&
        assert(secondSupplierCall)(equalTo(1))
    },
    testM("Correctly do an initial fetch from a supplier")(
      for {
        prefetcher          <- Prefetcher.withInitialFetch(-42, incrementer, 1.second).provideCustomLayer(logEnv)
        immediatelyHeld     <- prefetcher.currentValueRef.get
        _                   <- TestClock.adjust(1.second)
        initialSupplierCall <- prefetcher.currentValueRef.get
        _                   <- TestClock.adjust(1.second)
        secondSupplierCall  <- prefetcher.currentValueRef.get
      } yield assert(immediatelyHeld)(equalTo(-41)) &&
        assert(initialSupplierCall)(equalTo(-40)) &&
        assert(secondSupplierCall)(equalTo(-39))
    ),
    testM("Correctly do an initial fetch from a supplier that ignores the previous value") {
      val incr = new Incr().supplier
      for {
        prefetcher          <- Prefetcher.withInitialFetch(-42, incr, 1.second).provideCustomLayer(logEnv)
        immediatelyHeld     <- prefetcher.currentValueRef.get
        _                   <- TestClock.adjust(1.second)
        initialSupplierCall <- prefetcher.currentValueRef.get
        _                   <- TestClock.adjust(1.second)
        secondSupplierCall  <- prefetcher.currentValueRef.get
      } yield assert(immediatelyHeld)(equalTo(0)) &&
        assert(initialSupplierCall)(equalTo(1)) &&
        assert(secondSupplierCall)(equalTo(2))
    }
  )

  // clean incrementer
  private val incrementer = ZIO.fromFunction[Int, Int](i => i + 1)
}

// dirty incrementer, Should be avoided
class Incr() {
  private var counter  = -1

  val supplier = ZIO.effect {
    counter += 1
    counter
  }
}

class FailingCounter() {
  private var counter = -1

  val supplier = ZIO.effect {
    counter += 1

    if (counter % 2 == 0)
      ZIO.fail(new Exception("Damn!! It failed"))
    else
      ZIO.succeed(counter)
  }.flatten
}
