import anarchy.ds.compose.TreeView
import anarchy.ds.compose.TreeViewElement
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.system.measureTimeMillis


var id = 0

class MaTreeElement: TreeViewElement() {
    override val key: Any? = id++
    val showIcon = Random.nextBoolean()
    var checked =
        if (Random.nextBoolean())
            null
        else
            mutableStateOf(Random.nextBoolean())

    private
    val children_ = mutableListOf<MaTreeElement>()

    fun addChild(c: MaTreeElement){
        children_.add(c)
        notifyChildrenChanged()
    }

    @Composable
    override fun view() {
        Row (
            modifier = Modifier.height(30.dp),
            verticalAlignment =Alignment.CenterVertically
        ){
            var txt = "A text"
            if (showIcon) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = ""
                )
                txt += " + icon"
            }
            if (checked != null) {
                Checkbox(
                    modifier = Modifier.width(25.dp),
                    checked = checked!!.value,
                    onCheckedChange = { checked!!.value = !checked!!.value })
                txt += " + checkbox"
            }
            Text(
                text = txt
            )
        }
    }

    override fun hasChildren(): Boolean = children_.isNotEmpty()

    override fun children(): Iterable<MaTreeElement> {
        return children_.asIterable()
    }
}


fun makeElement() : MaTreeElement {
    val e = MaTreeElement()
    if (Random.nextInt(10) < 2){
        repeat(Random.nextInt(10)){
            e.addChild(makeElement())
        }
    }
    return e
}

@Composable
@Preview
fun App() {
    val root = makeElement()
    repeat(10){
        root.addChild(makeElement())
    }

    MaterialTheme {
        LaunchedEffect(null){
            val t = measureTimeMillis {
                val repeats = 100_000
                repeat(repeats) {
                    if (it % 1000 == 0) {
                        print("${100 * it / repeats}%\r")
                        delay(1)
                    }
                    root.addChild(makeElement())
                }
                println("done")
            }
            println("${t}ms")
        }
        Box {
            val state = rememberLazyListState()
            TreeView(root, elementModifier = { Modifier.fillParentMaxWidth() }, stateLazyList = state)
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }

    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

