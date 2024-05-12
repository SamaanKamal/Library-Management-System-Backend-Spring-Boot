package com.example.LibrarySystem.Service.BorrowingRecordService;

import com.example.LibrarySystem.Entity.Book;
import com.example.LibrarySystem.Entity.BorrowingRecord;
import com.example.LibrarySystem.Entity.Patron;
import com.example.LibrarySystem.Reposirtoy.BookRepository;
import com.example.LibrarySystem.Reposirtoy.BorrowingRecordRepository;
import com.example.LibrarySystem.Reposirtoy.PatronRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BorrowingRecordService implements IBorrowingRecordService{
    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PatronRepository patronRepository;
    @Override
    public BorrowingRecord borrowBook(Integer bookId, Integer patronId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->
                new RuntimeException("Book not found with id:"  + bookId));
        Patron patron= patronRepository.findById(patronId).orElseThrow(()->
                new RuntimeException("Patron not found with id:"  + patronId));
        BorrowingRecord borrowed = borrowingRecordRepository.findByBookAndPatron(book,patron).orElseThrow(
                ()-> new RuntimeException("Borrowing Record not Found with Book Id: " + book.getBookId()  + " and Patron Id: "+ patron.getPatronId())
        );
        if(borrowed!=null&&borrowed.getReturnDate()==null){
            throw new RuntimeException("Book is already borrowed by this patron!");
        }

//        if(!isBookAvailable(bookId)){
//            try {
//                throw new BadRequestException("Book is currently unavailable for borrowing!");
//            } catch (BadRequestException e) {
//                throw new RuntimeException(e);
//            }
//        }

        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setBook(book);
        borrowingRecord.setPatron(patron);
        borrowingRecord.setBorrowDate(LocalDate.now());
        BorrowingRecord savedBorrowingRecord = borrowingRecordRepository.save(borrowingRecord);
        if(savedBorrowingRecord!=null){
            return borrowingRecord;
        }
        return null;
    }

    @Override
    public boolean returnBook(Integer bookId, Integer patronId) {
        BorrowingRecord borrowingRecord = borrowingRecordRepository.findByBookAndPatron(bookRepository.findById(bookId).get(), patronRepository.findById(patronId).get())
                .orElseThrow(() -> new RuntimeException("Borrowing Record not Found with Book Id: " + bookId  + " and Patron Id: "+ patronId));

        if(borrowingRecord==null&&borrowingRecord.getReturnDate()!=null){
            throw new RuntimeException("Book is not currently borrowed by this patron!");
        }
        borrowingRecord.setReturnDate(LocalDate.now());

        BorrowingRecord savedBorrowingRecord =borrowingRecordRepository.save(borrowingRecord);
        if(savedBorrowingRecord!=null){
            return true;
        }
        return false;
    }

//    public boolean isBookAvailable(Integer bookId) {
//        return !borrowingRecordRepository.existsByBookIdAndReturnDateIsNull(bookId);
//    }
//    public boolean isBookBorrowedByPatron(Integer bookId, Integer patronId) {
//        return borrowingRecordRepository.existsByBookIdAndPatronIdAndReturnDateIsNull(bookId, patronId);
//    }
}
