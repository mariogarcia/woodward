package woodward

import org.jsoup.nodes.Document

import woodward.extractors.Text
import woodward.extractors.Title
import woodward.extractors.Dates
import woodward.extractors.Authors
import woodward.extractors.Articles
import woodward.extractors.CoverStory

/**
 *
 * @since 0.1.0
 */
class W {

  /**
   *
   * @param url
   * @return
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
   *
   * @param url
   * @return
   * @since 0.1.0
   */
  static Source readSource(String url) {
    Document doc = Network.getDocument(url)

    return new Source(
      coverStory: CoverStory.extract(url),
      articles: Articles.extract(url)
    )
  }
}
