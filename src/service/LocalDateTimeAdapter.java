package service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime ==null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(localDateTime.format(formatter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String date = jsonReader.nextString();
        if (date.isEmpty() || date.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(date, formatter);
    }
}
