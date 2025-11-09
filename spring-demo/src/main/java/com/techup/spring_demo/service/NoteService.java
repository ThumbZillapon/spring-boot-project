package com.techup.spring_demo.service;

import com.techup.spring_demo.dto.NoteRequest;
import com.techup.spring_demo.dto.NoteResponse;
import com.techup.spring_demo.entity.Note;
import com.techup.spring_demo.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {
    
    private final NoteRepository noteRepository;
    
    public NoteResponse createNote(NoteRequest noteRequest) {
        Note note = Note.builder()
                .title(noteRequest.getTitle())
                .content(noteRequest.getContent())
                .build();
        Note savedNote = noteRepository.save(note);
        return toNoteResponse(savedNote);
    }
    
    public List<NoteResponse> getAllNotes() {
        return noteRepository.findAll().stream()
                .map(this::toNoteResponse)
                .collect(Collectors.toList());
    }
    
    public Optional<NoteResponse> getNoteById(Long id) {
        return noteRepository.findById(id)
                .map(this::toNoteResponse);
    }
    
    @Transactional
    public Optional<NoteResponse> updateNote(Long id, NoteRequest noteRequest) {
        return noteRepository.findById(id)
                .map(note -> {
                    note.setTitle(noteRequest.getTitle());
                    note.setContent(noteRequest.getContent());
                    Note updatedNote = noteRepository.save(note);
                    return toNoteResponse(updatedNote);
                });
    }
    
    @Transactional
    public boolean deleteNote(Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private NoteResponse toNoteResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}

