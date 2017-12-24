package woodward.plan

import woodward.Article

class ArticlesPlan implements Plan<List<Article>> {

  List<String> uris

  /**
   * This method blocks until the value is computed
   *
   * return the result of the plan
   * @since 0.1.0
   */
  List<Article> get() {
    return uris
      .parallelStream()
      .map { new ArticlePlan(uri: it).get() }
      .collect()
  }
}
