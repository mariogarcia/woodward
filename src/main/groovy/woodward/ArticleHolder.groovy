package woodward

import woodward.util.Domain

/**
 * Represents a pointer to an {@link Article}.
 *
 * @since 0.1.0
 */
@Domain(includeNames = true)
class ArticleHolder {

  /**
   * The link to get the full {@link Article} from
   *
   * @since 0.1.0
   */
  String link
}
