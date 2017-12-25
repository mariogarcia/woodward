package woodward

import woodward.util.Domain

/**
 * A category is a set of articles sharing the same topic. E.g:
 * Sports, International...
 *
 * @since 0.1.0
 */
@Domain(includeNames = true, copyWith = true)
class Category {

  /**
   * Link where the article was taken from
   *
   * @since 0.1.0
   */
  String link

  /**
   * The name of the category. It should usually be something like:
   * Sports, Technology, International, Economics...
   *
   * @since 0.1.0
   */
  String name

  /**
   * List of {@link ArticleHolder} instances belonging to this
   * category
   *
   * @since 0.1.0
   */
  List<ArticleHolder> articles
}
