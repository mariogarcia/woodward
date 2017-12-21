package woodward

/**
 * @since 0.1.0
 */
class Utils {

  /**
   * @param action
   * @return
   * @since 0.1.0
   */
  static Object skipIfEmptyList(Closure action) {
    return action() ?: null
  }

  /**
   * Resolves the given file classpath to the underlying file system
   * full path as an {@link URL}
   *
   * @param filePath path within the classpath of a given resource
   * @return a {@link URL} of the passed string path
   * @since 0.1.0
   */
  static URL classpathAsURL(String filePath) {
    Class.getResource(filePath)
  }
}
