package com.example.votingapp.service;

import com.example.votingapp.model.User;
import com.example.votingapp.repository.UserRepository;
import org.apache.commons.csv.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;

@Service
public class UserImportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Main entry point
    public List<User> importUsers(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        if (fileName != null && fileName.endsWith(".csv")) {
            return importUsersFromCSV(file);
        } else if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
            return importUsersFromExcel(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type. Only CSV and Excel files are allowed.");
        }
    }

    // CSV Import
    private List<User> importUsersFromCSV(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()              // replaces withFirstRecordAsHeader()
                .setSkipHeaderRecord(true) // skip the header row
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader,format)) {

            for (CSVRecord record : csvParser) {
                try {
                    User user = parseUser(record.get("userId"), record.get("password"), record.get("isAdmin"));
                    users.add(user);
                } catch (Exception e) {
                    System.err.println("Skipping row (CSV): " + record.toString() + " -> " + e.getMessage());
                }
            }
        }

        return saveValidUsers(users);
    }

    // Excel Import
    private List<User> importUsersFromExcel(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip header row
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    String userId = row.getCell(0).getStringCellValue();
                    String password = row.getCell(1).getStringCellValue();
                    String isAdmin = row.getCell(2).getStringCellValue();

                    User user = parseUser(userId, password, isAdmin);
                    users.add(user);
                } catch (Exception e) {
                    System.err.println("Skipping row (Excel): Row " + row.getRowNum() + " -> " + e.getMessage());
                }
            }
        }

        return saveValidUsers(users);
    }

    // Shared user parsing logic with validation
    private User parseUser(String userId, String rawPassword, String adminFlag) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Missing userId");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Missing password");
        }

        boolean isAdmin = Boolean.parseBoolean(adminFlag);

        User user = new User();
        user.setUserId(userId.trim());
        user.setPassword(passwordEncoder.encode(rawPassword.trim()));
        user.setAdmin(isAdmin);
        user.setCanVote(!isAdmin); // default rule: admins can’t vote

        return user;
    }

    // Save users with duplicate check
    private List<User> saveValidUsers(List<User> users) {
        List<User> savedUsers = new ArrayList<>();
        for (User user : users) {
            if (userRepository.existsById(user.getUserId())) {
                System.err.println("Skipping duplicate userId: " + user.getUserId());
                continue;
            }
            savedUsers.add(userRepository.save(user));
        }
        return savedUsers;
    }
}
