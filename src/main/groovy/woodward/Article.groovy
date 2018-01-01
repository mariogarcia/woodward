package woodward

import woodward.util.Domain

/**
 * Represents an online article.
 *
 * @since 0.1.0
 */
@Domain(includeNames = true)
class Article {

  /**
   * The content of the article
   *
   * @since 0.1.0
   */
  String text

  /**
   * Direct link to article
   *
   * @since 0.1.0
   */
  String link

  /**
   * The title of the article
   *
   * @since 0.1.0
   */
  String title

  /**
   * The date when the article was published
   *
   * @since 0.1.0
   */
  String publishDate

  /**
   * The list of strings with the authors who signed the article
   *
   * @since 0.1.0
   */
  List<String> authors
}
