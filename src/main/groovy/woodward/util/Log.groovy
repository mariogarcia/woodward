package woodward.util

import groovy.util.logging.Slf4j
import groovy.transform.AnnotationCollector

/**
 * Annotation to enable logging in the annotated class
 *
 * @since 0.1.0
 * @see Slf4j
 */
@AnnotationCollector([Slf4j])
@interface Log {}
