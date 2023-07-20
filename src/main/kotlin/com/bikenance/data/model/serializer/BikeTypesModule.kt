package com.bikenance.data.model.serializer

import com.bikenance.data.model.BikeType
import com.bikenance.data.model.components.ComponentType
import com.bikenance.data.model.components.MaintenanceType
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule

class BikeTypesModule : SimpleModule() {
    init {
        addSerializer(ComponentType::class.java, ComponentTypeToStringSerializer())
        addDeserializer(ComponentType::class.java, StringToComponentTypeDeserializer())
        addSerializer(MaintenanceType::class.java, MaintenanceTypeToStringSerializer())
        addDeserializer(MaintenanceType::class.java, StringToMaintenanceTypeDeserializer())
        addSerializer(BikeType::class.java, BikeTypeToStringSerializer())
        addDeserializer(BikeType::class.java, StringToBikeTypeDeserializer())
    }
}

class ComponentTypeToStringSerializer : JsonSerializer<ComponentType>() {
    override fun serialize(value: ComponentType, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.name)
    }
}

class StringToComponentTypeDeserializer : JsonDeserializer<ComponentType>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ComponentType {
        return ComponentType.getByName(p.text)
    }

}

class MaintenanceTypeToStringSerializer : JsonSerializer<MaintenanceType>() {
    override fun serialize(value: MaintenanceType, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.name)
    }
}

class StringToMaintenanceTypeDeserializer : JsonDeserializer<MaintenanceType>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MaintenanceType {
        return MaintenanceType.getByName(p.text)
    }
}

class BikeTypeToStringSerializer : JsonSerializer<BikeType>() {
    override fun serialize(value: BikeType, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.name)
    }
}

class StringToBikeTypeDeserializer : JsonDeserializer<BikeType>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): BikeType {
        return BikeType.getByName(p.text)
    }

}