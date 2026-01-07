package ru.edenor.customAttributePotion.handler

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemSpawnEvent
import ru.edenor.customAttributePotion.CustomAttributePotion.Companion.plugin
import ru.edenor.customAttributePotion.CustomAttributePotion.Companion.storage
import ru.edenor.customAttributePotion.data.Potion
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

class VaultLootHandler : Listener {

  @EventHandler
  fun onItemSpawn(event: ItemSpawnEvent) {
    val itemEntity = event.entity
    val itemStack = itemEntity.itemStack
    val world = itemEntity.world

    if (world.environment != World.Environment.THE_END) return
    if (itemStack.type != Material.PAPER) return

    val meta = itemStack.itemMeta
    val displayNameComponent: Component = meta?.displayName() ?: return

    if (!displayNameComponent.hasDecoration(TextDecoration.BOLD) ||
      displayNameComponent.decoration(TextDecoration.BOLD) != TextDecoration.State.TRUE
    ) {
      return
    }

    val name = PlainTextComponentSerializer.plainText().serialize(displayNameComponent)
    val potion: Potion? = storage.getPotion(name)
    if (potion == null) {
      return
    }

    itemEntity.itemStack = potion.makePotion()
  }
}
