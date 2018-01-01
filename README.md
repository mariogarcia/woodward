# Woodward

**Woodward** is a library focused on retrieving articles from online
newspapers. This project is named after [Bob
Woodward](https://en.wikipedia.org/wiki/Bob_Woodward) famous because
of his reporting with Carl Bernstein on Watergate

## Feature list

The following lists describe the current status of the things you can
actually do with **Woodward**

### Front page

- [x] Retrieve front page categories
- [ ] Retrieve front page articles

### Categories

- [x] Get all categories
- [x] Filtering categories by name

### Article

- [x] Retrieve an article by a plain url string
- [x] Retrieve an article by an article holder
- [x] Retrieve title
- [x] Retrieve text
- [x] Retrieve publish date as string
- [ ] Retrieve publish date as a date

## How to

### Gradle

TODO

### How to get a specific Category

If you're only interested in a specific category, you can pass the
source url and the name of the category you're interested in.

```groovy
import woodward.*

Category cnnSports = W
  .categoriesIn("http://www.usatoday.com")
  .byName("sports")
  .single()
```

### Filtering articles

```groovy
import woodward.*

List<Article> filtered = W
  .articlesIn("http://www.cnn.com")
  .byTitle(".*Apple.*")
  .byCategory("tech")
  .all()
```
