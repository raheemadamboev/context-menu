package xyz.teamgravity.contextmenu

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun CardPerson(
    name: String,
    menus: List<PersonMenuModel>,
    onMenuClick: (PersonMenuModel) -> Unit,
) {
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }

    var menuVisible by rememberSaveable { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var height by remember { mutableStateOf(0.dp) }

    Card(
        modifier = Modifier
            .onSizeChanged { size ->
                height = with(density) { size.height.toDp() }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .indication(
                    interactionSource = interaction,
                    indication = LocalIndication.current
                )
                .pointerInput(true) {
                    detectTapGestures(
                        onLongPress = { offset ->
                            menuVisible = true
                            pressOffset = DpOffset(offset.x.toDp(), offset.y.toDp())
                        },
                        onPress = { offset ->
                            val press = PressInteraction.Press(offset)
                            interaction.emit(press)
                            tryAwaitRelease()
                            interaction.emit(PressInteraction.Release(press))
                        }
                    )
                }
                .padding(16.dp)
        ) {
            Text(text = name)
        }
        DropdownMenu(
            expanded = menuVisible,
            onDismissRequest = {
                menuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - height
            )
        ) {
            menus.forEach { menu ->
                DropdownMenuItem(
                    text = {
                        Text(text = menu.title)
                    },
                    onClick = {
                        menuVisible = false
                        onMenuClick(menu)
                    }
                )
            }
        }
    }
}