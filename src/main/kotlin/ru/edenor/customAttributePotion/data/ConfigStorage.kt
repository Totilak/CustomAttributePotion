package ru.edenor.customAttributePotion.data

import net.kyori.adventure.text.format.TextColor
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.ConfigurationSection
import ru.edenor.customAttributePotion.CustomAttributePotion
import ru.edenor.customAttributePotion.CustomAttributePotion.Companion.POTION_SECTION
import java.time.Duration

class ConfigStorage(private var config: Configuration) : Storage {

  init {
    reload()
  }

  override fun getPotions(): List<Potion> {
    val section = config.getConfigurationSection(POTION_SECTION) ?: return listOf()
    return section
        .getKeys(false)
        .map { k -> section.getConfigurationSection(k)!! }
        .map { s -> readTemplate(s) }
  }

  override fun getPotion(name: String): Potion? {
    val section = config.getConfigurationSection(POTION_SECTION) ?: return null
    val template = section.getConfigurationSection(name) ?: return null
    return readTemplate(template)
  }

  override fun reload() {
    val plugin = CustomAttributePotion.plugin
    plugin.saveDefaultConfig()
    plugin.reloadConfig()
    config = plugin.config

    val potions = getPotions()
    val invalidAttributes =
      potions.flatMap { potion ->
        potion.attributes.filter { it.bukkitAttribute == null }
      }

    if (invalidAttributes.isNotEmpty()) {
      plugin.logger.severe(
        "[CAP] Loaded with ${invalidAttributes.size} invalid attribute(s). " +
            "Some potion effects will be ignored."
      )
    } else {
      plugin.logger.info("[CAP] Config loaded successfully.")
    }
  }


  private fun readTemplate(section: ConfigurationSection): Potion {
    return Potion(
        name = section.name,
        title = section.getString("title") ?: "No name",
        attributes =
            parseAttributes(
                section.getConfigurationSection("attributes")
                    ?: throw IllegalArgumentException("${section.name} has no attributes!")),
        color = TextColor.fromHexString(section.getString("color") ?: "#bfff00")!!,
        duration = Duration.ofSeconds(section.getLong("duration")),
        description = section.getString("description") ?: "No description",
        particleType = section.getString("particleType")?.uppercase()?.let(Particle::valueOf))
  }

  private fun parseAttributes(section: ConfigurationSection): List<ConfigAttribute> =
    section.getKeys(false).mapNotNull { key ->
      val namespacedKey =
        NamespacedKey.fromString(key)
          ?: run {
            CustomAttributePotion.plugin.logger.severe(
              "[CAP] Invalid attribute key syntax: '$key' " +
                  "in ${section.currentPath}"
            )
            return@mapNotNull null
          }

      val value = section.getDouble(key)
      ConfigAttribute(namespacedKey, value)
    }


}
