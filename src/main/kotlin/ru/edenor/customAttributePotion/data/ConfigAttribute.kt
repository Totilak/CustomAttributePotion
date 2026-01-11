package ru.edenor.customAttributePotion.data

import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import ru.edenor.customAttributePotion.CustomAttributePotion

data class ConfigAttribute(
  val key: NamespacedKey,
  val value: Double
) {
  val bukkitAttribute: Attribute? =
    Registry.ATTRIBUTE.get(key).also { attribute ->
      if (attribute == null) {
        CustomAttributePotion.plugin.logger.severe(
          "[CAP] Unknown or unsupported attribute '$key'. " +
              "This attribute will be ignored."
        )
      }
    }
}
