# Tree View control for compose multiplatform

Based on LazyList and hence is capable of handling > 100k of elements in it.

## Usage

To use it in your app, you have to implement a simple interface

``` kotlin
abstract class TreeViewElement {
    /**
    View to display as the tree element.
     */
    @Composable
    abstract fun view()
    /**
     Does it have subelements?
     */
    abstract fun hasChildren(): Boolean
    /**
    The list of sub elements.
    The only member which makes sense for root.
    Children are shown in the iteration order.
     */
    abstract fun children(): Iterable<TreeViewElement>
    /**
    Make a call to this, when amount of children or their positions are changed
     */
    fun notifyChildrenChanged() = currentScope?.run{ invalidate() }
    /**
    A stable and unique key representing the item. Using the same key for multiple items in the tree is not allowed.
    Type of the key should be saveable via Bundle on Android. If null is passed the position in the tree will represent
    the key. Which might be ok for immutable trees.
    When you specify the key the scroll position will be maintained based on the key, which means if you add/remove
    items before the current visible item, the item with the given key will be kept as the first visible one.
    This can be overridden by calling 'requestScrollToItem' on the [LazyListState].
     */
    open
    val key: Any? = null
    /**
    The type of the content of this item. The item compositions of the same type could be reused more efficiently.
    Note that null is a valid type and items of such type will be considered compatible.
     */
    open
    val contentType: Any? = null
    /**
    If this element [has children][hasChildren], this controls whether they are shown right now
     */
    var isFolded: MutableState<Boolean> = mutableStateOf(true)

    internal
    var currentScope: RecomposeScope? = null
}
```

You decide, how are you going to store your tree elements (Hash, List etc).
Then you pass the root element (not itself shown) to `TreeView` composable function.
See the `Main.kt` for usage example.

## Build setup

The lib is on **Maven Central**, so just add 

`implementation("io.gitlab.kompose:tree-view:0.0.6")`

To your gradle script

