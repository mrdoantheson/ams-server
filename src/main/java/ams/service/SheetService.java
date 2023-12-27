package ams.service;

import ams.model.dto.ImportResult;
import ams.sheet.SheetRowData;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface SheetService {

    <T, E extends SheetRowData> ImportResult importFile(MultipartFile multipartFile,
                                                        String sheetName,
                                                        Class<E> sheetRowData,
                                                        Class<T> objectType) throws IOException;

    <T, E extends SheetRowData> ImportResult importFileToClass(MultipartFile multipartFile,
                                                               String sheetName,
                                                               Class<E> sheetRowData,
                                                               Class<T> objectType,
                                                               Long clazzId) throws IOException;

    <T, E extends SheetRowData> ByteArrayInputStream exportFile(InputStream inputStream,
                                                                String sheetName,
                                                                Class<E> sheetRowData,
                                                                Iterable<T> data) throws IOException;
}
