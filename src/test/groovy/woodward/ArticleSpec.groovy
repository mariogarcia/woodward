package woodward

import spock.lang.Ignore
import spock.lang.Specification
import woodward.plan.ArticlePlan

class ArticleSpec extends Specification {


  def "someLibraryMethod returns true"() {
    when:
//      Article article = W
//      .article("http://edition.cnn.com/2017/12/19/politics/republican-tax-plan-vote/index.html")

//    List<Article> articles = W
//      .articles("http://www.elmundo.es/cataluna/2017/12/19/5a3986a4268e3ed7478b45f5.html",
//                "https://www.thesun.co.uk/motors/5163233/cops-seize-car-being-driven-home-from-work-as-its-driver-wasnt-insured-for-commuting/",
//                "https://www.thesun.co.uk/motors/5163233/cops-seize-car-being-driven-home-from-work-as-its-driver-wasnt-insured-for-commuting/")

//
    List<Article> filtered = W
      .articlesIn("http://www.cnn.com")
      .byTitle(".*Apple.*")
      .byCategory("tech")
      .all()

//
//    Category cnnSports = W
//      .categoriesIn("http://www.cnn.com")
//      .byName("Sports")
//      .single()

      println(filtered)

    then:
      filtered.size() > 0
  }
}
