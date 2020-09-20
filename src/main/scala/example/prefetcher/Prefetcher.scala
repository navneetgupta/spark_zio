package example.prefetcher

import java.util.function.Supplier

import zio._
import zio.duration.{Duration, _}
case class Prefetcher[T](
                          prefetchedValueRef: Ref[T],
                          val updateFiber: Fiber[Throwable, Any]
                        ) {
  val currentValueRef = prefetchedValueRef.readOnly
}

object Prefetcher {

  def withInitialValue[T](
                           initialValue: T,
                           supplier: ZIO[T, Throwable, T],
                           updateInterval: Duration,
                           initialWait: Duration = 0.seconds
                         ): Unit =
    for {
      initialValueRef <- Ref.make(initialValue)
      updateFiber <- scheduleUpdate(initialValueRef, supplier, updateInterval, initialWait).fork
    } yield new Prefetcher[T](initialValueRef, updateFiber)

  def scheduleUpdateWithInitialDelay[T](
                                         valueRef: Ref[T],
                                         supplier: ZIO[T, Throwable, T],
                                         updateInterval: Duration) =
   scheduleUpdate(valueRef, supplier, updateInterval, 0.seconds)
     .delay(updateInterval)

  def withIntitalFetch[T](
                         zero: T,
                         supplier: ZIO[T, Throwable, T],
                         updateInterval: Duration
                         ): Unit =
    for {
      initalValue <- supplier.provide(zero)
      initialValueRef <- Ref.make(initalValue)
      updateFiber <- scheduleUpdateWithInitialDelay(initialValueRef, supplier, updateInterval).fork
  } yield new Prefetcher[T](initialValueRef, updateFiber)

  def scheduleUpdate[T](
                         valueRef: Ref[T],
                         supplier: ZIO[T, Throwable, T],
                         updateInterval: Duration,
                         initialWait: Duration) =
    updatePrefetchedValueRef(valueRef, supplier)
      .retry(Schedule.spaced(updateInterval))
      .repeat(Schedule.spaced(updateInterval)).delay(initialWait)

  def updatePrefetchedValueRef[T](
                                   valueRef: Ref[T],
                                   supplier: ZIO[T, Throwable, T]) =
    for {
      previousVal <- valueRef.get
      newVal <- supplier
        .provide(previousVal)
        .onError(err =>
          log.error(
            "Evaluation of the supplier failed, prefetched value not updated: " +
              err.failureOption.map(_.getMessage).getOrElse("")
          ))
      updatedRef <- valueRef.set(newVal)
    } yield ()
}
