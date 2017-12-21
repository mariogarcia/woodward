package woodward

import groovy.transform.ToString
import groovy.transform.Immutable

/**
 * @since 0.1.0
 */
@Immutable
@ToString(includeNames = true)
class Article {
  /**
   * @since 0.1.0
   */
  String text

  /**
   * @since 0.1.0
   */
  String title

  /**
   * @since 0.1.0
   */
  String publishDate

  /**
   * @since 0.1.0
   */
  List<String> authors
}
