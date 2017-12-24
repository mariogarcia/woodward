package woodward.plan

import java.util.concurrent.CompletableFuture

/**
 * Represents an execution plan to get values of type T
 *
 * @param T the result of the computation plan
 * @since 0.1.0
 */
trait Plan<T> {

  /**
   * This method blocks until the value is computed
   *
   * return the result of the plan
   * @since 0.1.0
   */
  abstract T get()
}
