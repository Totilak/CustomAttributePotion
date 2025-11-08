package ru.edenor.customAttributePotion.handler

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.persistence.PersistentDataType
import ru.edenor.customAttributePotion.CustomAttributePotion.Companion.potionKey
import ru.edenor.customAttributePotion.CustomAttributePotion.Companion.storage
import ru.edenor.customAttributePotion.CustomAttributePotionService
import ru.edenor.customAttributePotion.CustomAttributePotionService.getPotionData

class PlayerHandler : Listener {

  @EventHandler
  fun onDrink(event: PlayerItemConsumeEvent) {
    val player = event.player
    val item = event.item

    if (item.type == Material.MILK_BUCKET) {
      CustomAttributePotionService.clearPotionEffects(player)
      return
    }

    if (item.type != Material.POTION || !item.hasItemMeta()) return

    val pdc = item.itemMeta.persistentDataContainer
    val potionName = pdc.get(potionKey, PersistentDataType.STRING) ?: return
    val potion = storage.getPotion(potionName) ?: return

    CustomAttributePotionService.applyPotion(player, potion)
    CustomAttributePotionService.effectOnDrink(player, potion)
  }

  @EventHandler
  fun onRespawn(event: PlayerRespawnEvent) {
    CustomAttributePotionService.clearPotionEffects(event.player)
  }

  @EventHandler
  fun onJoin(event: PlayerJoinEvent) {
    val player = event.player
    val potionData = player.getPotionData()
    if (potionData.isNotEmpty()) {
      CustomAttributePotionService.startTask(player)
      CustomAttributePotionService.sendPotionInfo(player)
    }
  }

  @EventHandler
  fun onQuit(event: PlayerQuitEvent) {
    val player = event.player
    CustomAttributePotionService.stopTaskAndSaveRemaining(player)
  }
}
