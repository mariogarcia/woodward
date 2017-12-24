package woodward.util

/**
 * Some miscelaneous functions
 *
 * @since 0.1.0
 */
class Misc {

  /**
   * This function can be used along with
   * <b>[collection].findResult(Closure)</b> in an expression like
   * <b>listOfExecutions.findResult(Misc.&skipIfEmptyList)</b>
   *
   * The problem with <b>findResult</b> is that it doesn't use the
   * Groovy truth to evaluate the outcome. That's why this function
   * applies Groovy truth to the behavior of the <b>findResult</b>
   * function
   *
   * @param action action to be executed
   * @return the result of the action if the result is other than null
   * or null
   * @since 0.1.0
   */
  static Object skipIfEmptyList(Closure action) {
    return action() ?: null
  }

  /**
   * Composes two predicates into one, following the semantics
   * <b>predLeft && predRight</b>
   *
   * @param predLeft first predicate to be evaluated
   * @param predRight second predicate to be evaluated
   * @return a predicate so that <b>predLeft && predRight</b>
   * @since 0.1.0
   */
  static Closure<Boolean> and(Closure<Boolean> predLeft, Closure<Boolean> predRight) {
    return { item ->
      return predLeft(item) && predRight(item)
    }
  }

  static Closure executeClosure() {
    return { Closure closure -> closure() }
  }
}
