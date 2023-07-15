package com.bikenance.model.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val iso8061DateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

fun String.iso8061ToLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this, iso8061DateFormatter)
}

fun LocalDateTime.formatAsIso8061(): String {
    return iso8061DateFormatter.format(this)
}

class DateTimeModule : SimpleModule() {
    init {
        addSerializer(LocalDateTime::class.java, LocalDateTimeToStringSerializer())
        addDeserializer(LocalDateTime::class.java, StringToLocalDateTimeDeserializer())
    }
}

class LocalDateTimeToStringSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.formatAsIso8061())
    }
}

class StringToLocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        return p.text.iso8061ToLocalDateTime()
    }

}