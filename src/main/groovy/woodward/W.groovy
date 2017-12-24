package woodward

import org.jsoup.nodes.Document

import woodward.extractors.Text
import woodward.extractors.Title
import woodward.extractors.Dates
import woodward.extractors.Authors
import woodward.extractors.Categories
import woodward.util.Network
import woodward.plan.SourcePlan
import woodward.plan.ArticlePlan
import woodward.plan.ArticlesPlan
import woodward.plan.ArticlesInPlan

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
  static Article article(String uri) {
    return new ArticlePlan(uri: uri).get()
  }

  /**
   * Given a pointer to a specific article this function resolves the
   * full content of the article
   *
   * @param articleHolder pointer to the real {@link Article}
   * @return the resolved {@link Article} instance
   * @since 0.1.0
   */
  static Article article(ArticleHolder articleHolder) {
    return articleHolder?.link?.collect(W.&readArticle)?.find()
  }

  /**
   * Loads the content of an articles located at the uris passed as
   * arguments
   *
   * @param uris the location of the articles
   * @return an list of {@link Article} instances
   * @since 0.1.0
   */
  static List<Article> articles(String... uris) {
    return new ArticlesPlan(uris: uris as List).get()
  }

  /**
   * Builds a plan to get filtered articles
   *
   * @param uri the uri to get the articles from
   * @return an instance of {@link ArticlesInPlan}
   * @since 0.1.0
   */
  static ArticlesInPlan articlesIn(String uri) {
    return new ArticlesInPlan(uri: uri)
  }

  /**
   * Retrieves the content of a given source. A {@link Source} is like
   * the root site of an online newspaper, the frontpage so to speak.
   *
   * @param url the location of the source
   * @return an instance of {@link Source}
   * @since 0.1.0
   */
  static Source source(String uri) {
    return new SourcePlan(uri: uri).get()
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
