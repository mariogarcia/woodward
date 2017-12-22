package woodward.util

import groovy.transform.ToString
import groovy.transform.Immutable
import groovy.transform.AnnotationCollector

/**
 * Annotation to mark a given class as immutable and capable of
 * representing itself in a meaningful way as an string
 *
 * @since 0.1.0
 * @see ToString
 * @see Immutable
 */
@AnnotationCollector([Immutable, ToString])
@interface Domain {}
