package woodward.plan

import woodward.Article
import woodward.util.Misc
import woodward.util.Network
import woodward.extractors.Text
import woodward.extractors.Title
import woodward.extractors.Dates
import woodward.extractors.Authors

class ArticlePlan implements Plan<Article> {

  String uri

  /**
   * This method blocks until the value is computed
   *
   * return the result of the plan
   * @since 0.1.0
   */
  Article get() {
    def document = Network.getDocument(uri)
    def computations = [{-> Text.extract(document) },
                        {-> Title.extract(document) },
                        {-> Authors.extract(document) },
                        {-> Dates.PublishDate.extract(document) }]

    def (text, title, authors, publishDate) = computations
      .parallelStream()
      .map(Misc.executeClosure())
      .collect()

    return new Article(text: text,
                       link: uri,
                       title: title,
                       authors: authors,
                       publishDate: publishDate)
  }
}
