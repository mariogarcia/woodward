package woodward

import org.jsoup.nodes.Document

import woodward.extractors.Text
import woodward.extractors.Title
import woodward.extractors.Dates
import woodward.extractors.Authors
import woodward.extractors.Categories
import woodward.util.Network

/**
 * Set of functions to retrieve front pages ({@link Source}) or
 * articles {@link Article} from an online newspaper
 *
 * @since 0.1.0
 */
class W {

  /**
   * Loads the content of an article located at the url passed as an
   * argument
   *
   * @param url the location of the article
   * @return an instance of {@link Article} with the full content of
   * the article
   * @since 0.1.0
   */
  static Article readArticle(String url) {
    Document doc = Network.getDocument(url)

    return new Article(
      text: Text.extract(doc),
      title: Title.extract(doc),
      authors: Authors.extract(doc),
      publishDate: Dates.PublishDate.extract(doc)
    )
  }

  /**
   * Given a pointer to a specific article this function resolves the
   * full content of the article
   *
   * @param articleHolder pointer to the real {@link Article}
   * @return the resolved {@link Article} instance
   * @since 0.1.0
   */
  static Article readArticle(ArticleHolder articleHolder) {
    return articleHolder?.link?.collect(W.&readArticle)?.find()
  }

  /**
   * Retrieves the content of a given source. A {@link Source} is like
   * the root site of an online newspaper, the frontpage so to speak.
   *
   * @param url the location of the source
   * @return an instance of {@link Source}
   * @since 0.1.0
   */
  static Source readSource(String url) {
    Document doc = Network.getDocument(url)

    return new Source(
      categories: Categories.extract(doc)
    )
  }

  /**
   * Loads only a specific category of a given source.
   *
   * @param url the url of the online newspaper
   * @param name the name of the category we want
   * @return an instance of {@link Category}
   * @since 0.1.0
   */
  static Category loadCategory(String url, String name) {
    Document doc = Network.getDocument(url)
    Category category = Categories.extractCategory(doc, name)

    return category
  }
}
