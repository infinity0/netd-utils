package netd.async.annot;

import java.lang.annotation.*;

/**
** Denotes a method which does not block.
**
** Formally, this does not include methods that merely take a long time to
** execute. However, depending on your application, you make wish to split up
** such methods into smaller chunks. The entire purpose of this library is to
** make such a refactor simple.
*/
@Target(value={ElementType.METHOD})
@Documented
@Inherited /* doesn't work on methods yet but stick it in here anyway */
public @interface Nonblocking {

}
