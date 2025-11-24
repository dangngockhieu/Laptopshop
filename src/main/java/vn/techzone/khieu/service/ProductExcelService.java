package vn.techzone.khieu.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import vn.techzone.khieu.entity.Product;
import vn.techzone.khieu.repository.ProductRepository;

@Setter
@Getter
@RequiredArgsConstructor
@Service
public class ProductExcelService {
    private final FileService fileService;
    private final ProductRepository productRepository;

    @Value("${upload-file.base-url}")
    private String baseURI;

    private String getStringCell(Row row, int index) {
        var cell = row.getCell(index);
        if (cell == null)
            return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> null;
        };
    }

    private Integer getIntCell(Row row, int index) {
        var cell = row.getCell(index);
        if (cell == null)
            return null;
        return (int) cell.getNumericCellValue();
    }

    public void importFromExcel(MultipartFile file) throws Exception {
        String folder = "temp_excel";
        fileService.createDirectory(folder);

        // Lưu file tạm thời bằng FileService
        String finalName = fileService.store(file, folder);

        // Đường dẫn file vừa lưu để đọc lại
        Path filePath = Paths.get(new URI(baseURI + folder + "/" + finalName));

        try (InputStream is = Files.newInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Product> products = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;
                if (getStringCell(row, 0) == null || getStringCell(row, 0).isBlank())
                    break;

                Product p = new Product();
                p.setName(getStringCell(row, 0));
                p.setOriginalPrice(getIntCell(row, 1));
                p.setCoupon(getIntCell(row, 2));
                p.setPrice(getIntCell(row, 1) - getIntCell(row, 2) * getIntCell(row, 1) / 100);
                p.setQuantity(getIntCell(row, 3));
                p.setWarranty(getStringCell(row, 4));
                p.setInfor(getStringCell(row, 5));
                p.setCpu(getStringCell(row, 6));
                p.setRam(getStringCell(row, 7));
                p.setStorage(getStringCell(row, 8));
                p.setScreen(getStringCell(row, 9));
                p.setGraphicsCard(getStringCell(row, 10));
                p.setBattery(getStringCell(row, 11));
                p.setWeight(getStringCell(row, 12));
                p.setReleaseYear(getStringCell(row, 13));
                p.setCategory(getStringCell(row, 14));
                p.setFactory(getStringCell(row, 15));

                products.add(p);
            }

            // Lưu vào Database
            productRepository.saveAll(products);

        } finally {
            // Đọc xong là XÓA NGAY (Dùng hàm delete của bạn)
            fileService.delete(folder, finalName);
            System.out.println("Đã xóa file tạm: " + finalName);
        }
    }
}