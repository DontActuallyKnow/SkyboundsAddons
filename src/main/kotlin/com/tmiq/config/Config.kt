package com.tmiq.config

import com.google.common.collect.Lists
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.ConfigField
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.autogen.*
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.gui.ValueFormatters
import dev.isxander.yacl3.platform.YACLPlatform
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.text.Text
import java.awt.Color

object Config {

    val INSTANCE: ConfigClassHandler<Config> = ConfigClassHandler.createBuilder(Config::class.java)
        .id(YACLPlatform.rl("skyboundsaddons", "config"))
        .serializer { config ->
            GsonConfigSerializerBuilder.create(config)
                .setPath(YACLPlatform.getConfigDir().resolve("sba-config.json5"))
                .setJson5(true)
                .build()
        }.build()

    @AutoGen(category = "test", group = "master_test")
    @TickBox
    @SerialEntry(comment = "This is a cool comment omg this is amazing")
    var testTickBox: Boolean = true

    @AutoGen(category = "test", group = "master_test")
    @dev.isxander.yacl3.config.v2.api.autogen.Boolean(
        formatter = dev.isxander.yacl3.config.v2.api.autogen.Boolean.Formatter.YES_NO,
        colored = true
    )
    @SerialEntry(comment = "This is a cool comment omg this is amazing")
    var testBoolean: Boolean = true

    @AutoGen(category = "test", group = "master_test")
    @IntSlider(min = 0, max = 10, step = 2)
    @SerialEntry
    var testInt: Int = 0

    @AutoGen(category = "test", group = "master_test")
    @DoubleSlider(min = 0.1, max = 10.2, step = 0.1)
    @SerialEntry
    var testDouble: Double = 0.1

    @AutoGen(category = "test", group = "master_test")
    @FloatSlider(min = 0.0f, max = 1f, step = 0.01f)
    @CustomFormat(ValueFormatters.PercentFormatter::class)
    @CustomName("A cool percentage.")
    @SerialEntry
    var testFloat: Float = 0.1f

    @AutoGen(category = "test", group = "master_test")
    @LongSlider(min = 0, max = 10, step = 2)
    @SerialEntry
    var testLong: Long = 0

    @AutoGen(category = "test", group = "master_test")
    @IntField(min = 0, max = 10)
    @SerialEntry
    var testIntField: Int = 0

    @AutoGen(category = "test", group = "master_test")
    @DoubleField(min = 0.1, max = 10.2)
    @SerialEntry
    var testDoubleField: Double = 0.1

    @AutoGen(category = "test", group = "master_test")
    @FloatField(min = 0.1f, max = 10.2f)
    @SerialEntry
    var testFloatField: Float = 0.1f

    @AutoGen(category = "test", group = "master_test")
    @LongField(min = 0, max = 10)
    @SerialEntry
    var testLongField: Long = 0

    @AutoGen(category = "test", group = "master_test")
    @EnumCycler
    @SerialEntry
    var testEnum: Alphabet = Alphabet.A

    @AutoGen(category = "test", group = "master_test")
    @ColorField
    @SerialEntry
    var testColor: Color = Color(0xFF0000FF.toInt(), true)

    @AutoGen(category = "test", group = "master_test")
    @StringField
    @SerialEntry
    var testString: String = "Test string"

    @AutoGen(category = "test", group = "master_test")
    @Dropdown(values = ["Apple", "Banana", "Cherry", "Date"], allowAnyValue = true)
    @SerialEntry
    var testDropdown: String = "Cherry"

    @AutoGen(category = "test", group = "master_test")
    @ItemField
    @SerialEntry
    var testItem: Item = Items.AZURE_BLUET

    @AutoGen(category = "test", group = "misc")
    @Label
    val testLabel: Text = Text.literal("Test label")

    @AutoGen(category = "test")
    @ListGroup(valueFactory = TestListFactory::class, controllerFactory = TestListFactory::class)
    @SerialEntry
    var testList: List<String> = Lists.newArrayList("A", "B")

    enum class Alphabet : NameableEnum {
        A, B, C;

        override fun getDisplayName(): Text {
            return Text.literal(name)
        }
    }

    class TestListFactory : ListGroup.ValueFactory<String>, ListGroup.ControllerFactory<String> {
        override fun provideNewValue(): String {
            return ""
        }

        override fun createController(
            annotation: ListGroup,
            field: ConfigField<List<String>>,
            storage: OptionAccess,
            option: Option<String>
        ): ControllerBuilder<String> {
            return StringControllerBuilder.create(option)
        }
    }

    fun getScreen(parent: Screen?): Screen {
        return INSTANCE.generateGui().generateScreen(parent)
    }
}