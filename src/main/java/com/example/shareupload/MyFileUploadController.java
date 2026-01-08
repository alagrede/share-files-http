package com.example.shareupload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MyFileUploadController {

    @Value("${uploadPath}")
    private String uploadRootPath;

    @RequestMapping(value = "/")
    public String homePage(Model model) throws IOException {
        MyUploadForm myUploadForm = new MyUploadForm();
        model.addAttribute("myUploadForm", myUploadForm);
        model.addAttribute("existingFiles", readFiles());
        model.addAttribute("ip", getIp());
        return "upload";
    }


    // POST: Do Upload
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String uploadOneFileHandlerPOST(HttpServletRequest request, //
                                           Model model, //
                                           @ModelAttribute("myUploadForm") MyUploadForm myUploadForm) throws IOException {

        return this.doUpload(request, model, myUploadForm);
    }


    private String doUpload(HttpServletRequest request, Model model, //
                            MyUploadForm myUploadForm) throws IOException {

        String description = myUploadForm.getDescription();
        System.out.println("Description: " + description);

        // Root Directory.
        //String uploadRootPath = request.getServletContext().getRealPath("upload");
        System.out.println("uploadRootPath=" + uploadRootPath);

        File uploadRootDir = new File(uploadRootPath);
        // Create directory if it not exists.
        //if (!uploadRootDir.exists()) {
        //    uploadRootDir.mkdirs();
        //}
        MultipartFile[] fileDatas = myUploadForm.getFileDatas();
        //
        List<File> uploadedFiles = new ArrayList<File>();
        List<String> failedFiles = new ArrayList<String>();

        for (MultipartFile fileData : fileDatas) {

            // Client File Name
            String name = fileData.getOriginalFilename();
            System.out.println("Client File Name = " + name);

            if (name != null && name.length() > 0) {
                try {
                    // Create the file at server
                    File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);

                    try (BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(serverFile))) {
                        fileData.getInputStream().transferTo(stream);
                    }

                    uploadedFiles.add(serverFile);
                    System.out.println("Write file: " + serverFile);
                } catch (Exception e) {
                    System.out.println("Error Write file: " + name);
                    failedFiles.add(name);
                }
            }
        }

        model.addAttribute("description", description);
        model.addAttribute("uploadedFiles", uploadedFiles);
        model.addAttribute("failedFiles", failedFiles);
        model.addAttribute("existingFiles", readFiles());
        model.addAttribute("ip", getIp());
        return "uploadResult";
    }

    private Map<String, String> readFiles() throws IOException {
        File uploadRootDir = new File(uploadRootPath);
        return Arrays.stream(uploadRootDir.listFiles()).collect(Collectors.toMap(k -> k.getName(), v -> readableFileSize(v.length())));
    }

    private String getIp() {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    String address = i.getHostAddress();
                    //System.out.println(address);
                    if (address.contains(".")
                            && isNumeric(address.split("\\.")[0])
                            && isValidIp(address.split("\\.")[0])) {
                        System.out.println("selected address: " + address);
                        return address;
                    }

                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return "";
    }

    public static boolean isValidIp(String strNum) {
        int number = Integer.parseInt(strNum);
        return (127 < number && number <= 192);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}