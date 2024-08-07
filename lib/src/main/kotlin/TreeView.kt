package anarchy.ds.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
/**
 The [root] itself is not shown. Only it's children

 @param elementModifier modifier used for each tree element
 @param childrenIndent indentation from left used to indent children
 */
@Composable
fun TreeView(
    root: TreeViewElement,
    modifier: Modifier = Modifier,
    elementModifier: LazyItemScope.() -> Modifier = { Modifier },
    stateLazyList: LazyListState = rememberLazyListState(),
    childrenIndent: Dp = 20.dp,
    directoryFoldView: @Composable (isFolded: Boolean) -> Unit = { folded ->
        val rotation by animateFloatAsState(
            if (folded) 0f else 90f
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Unfold",
            modifier = Modifier.graphicsLayer {
                rotationZ = rotation
            }.fillMaxSize()
        )
    },
    directoryFoldViewWidth: Dp = 20.dp,
){
    root.currentScope = currentRecomposeScope
    LazyColumn(modifier = modifier, state = stateLazyList) {
        AddRecursively(root, elementModifier, childrenIndent, directoryFoldView, directoryFoldViewWidth)
    }
}

@OptIn(ExperimentalFoundationApi::class)
internal
fun LazyListScope.AddRecursively(
    parent: TreeViewElement,
    elementModifier: LazyItemScope.() -> Modifier,
    childrenIndent: Dp,
    directoryFoldView: @Composable (isFolded: Boolean) -> Unit,
    directoryFoldViewWidth: Dp,
    level: Int = 0
){
    for (c in parent.children()){
        c.currentScope = parent.currentScope
        item(c.key, c.contentType){
            Row(
                modifier = elementModifier().padding(childrenIndent*level, 0.dp, 0.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                if (c.hasChildren()) {
                    Box(
                        modifier = Modifier.requiredWidth(directoryFoldViewWidth)
                            .onClick {
                                c.isFolded.value = !c.isFolded.value
                            }
                    ) {
                        directoryFoldView(c.isFolded.value)
                    }
                }
                else{
                    Spacer(modifier = Modifier.requiredWidth(directoryFoldViewWidth))
                }
                c.view()
            }
        }
        if (!c.isFolded.value)
            AddRecursively(c, elementModifier, childrenIndent, directoryFoldView, directoryFoldViewWidth,level + 1)
    }
}