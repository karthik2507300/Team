package com.certifypro.util;

import com.certifypro.model.Candidate;
import com.certifypro.model.CertificationProgram;
import com.certifypro.model.ExamWindow;
import com.certifypro.model.SeatAllocation;
import com.certifypro.model.TestCentre;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

/** Server-side PDF generation for candidate hall tickets (OpenPDF). */
@Component
public class PdfGenerator {

    public byte[] generateHallTicket(SeatAllocation alloc, Candidate candidate,
                                     ExamWindow window, TestCentre centre,
                                     CertificationProgram program) {
        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font sub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        Paragraph heading = new Paragraph("CertifyPro — Examination Hall Ticket", title);
        heading.setAlignment(Element.ALIGN_CENTER);
        heading.setSpacingAfter(6f);
        doc.add(heading);

        Paragraph hallNo = new Paragraph("Hall Ticket No: " + nz(alloc.getHallTicketNumber()), sub);
        hallNo.setAlignment(Element.ALIGN_CENTER);
        hallNo.setSpacingAfter(16f);
        doc.add(hallNo);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addRow(table, "Candidate", nz(candidate == null ? null : candidate.getName()));
        addRow(table, "Programme", nz(program == null ? null : program.getProgramName()));
        addRow(table, "Level", program == null || program.getLevel() == null ? "-" : program.getLevel().name());
        addRow(table, "Exam", nz(window == null ? null : window.getExamName()));
        addRow(table, "Exam Dates",
                (window == null ? "-" : nz(String.valueOf(window.getStartDate())))
                        + " to " + (window == null ? "-" : nz(String.valueOf(window.getEndDate()))));
        addRow(table, "Test Centre", nz(centre == null ? null : centre.getCentreName()));
        addRow(table, "Centre Address",
                (centre == null ? "-" : (nz(centre.getAddress()) + ", " + nz(centre.getCity()))));
        addRow(table, "Room", nz(alloc.getRoomNumber()));
        addRow(table, "Seat", nz(alloc.getSeatNumber()));
        addRow(table, "Status", alloc.getStatus() == null ? "-" : alloc.getStatus().name());

        doc.add(table);

        Paragraph note = new Paragraph(
                "\nPlease carry a valid photo ID. Report 30 minutes before the exam start time.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
        doc.add(note);

        doc.close();
        return out.toByteArray();
    }

    private void addRow(PdfPTable table, String label, String value) {
        PdfPCell l = new PdfPCell(new Phrase(label,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        PdfPCell v = new PdfPCell(new Phrase(value,
                FontFactory.getFont(FontFactory.HELVETICA, 11)));
        l.setPadding(6f);
        v.setPadding(6f);
        table.addCell(l);
        table.addCell(v);
    }

    private String nz(String s) {
        return (s == null || "null".equals(s)) ? "-" : s;
    }
}
