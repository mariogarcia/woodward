package woodward

import woodward.util.Domain

/**
 * A source normally represents the main page of an online newspaper,
 * a front page so to speak. In a source page we can find how articles
 * are organized into categories.
 *
 * @since 0.1.0
 */
@Domain(includeNames = true)
class Source {

  /**
   * List of {@link Category} found in the newspaper
   *
   * @since 0.1.0
   */
  List<Category> categories

  /**
   * A list of ALL article pointers found in ALL categories
   *
   * @since 0.1.0
   */
  List<ArticleHolder> articles
}
