package woodward.util

/**
 *
 * @since 0.1.0
 */
class Misc {

  /**
   * @param action
   * @return
   * @since 0.1.0
   */
  static Object skipIfEmptyList(Closure action) {
    return action() ?: null
  }
}
