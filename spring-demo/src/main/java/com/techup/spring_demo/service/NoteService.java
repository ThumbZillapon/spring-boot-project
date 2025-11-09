package com.techup.spring_demo.service;

import com.techup.spring_demo.entity.Note;
import com.techup.spring_demo.repository.NoteRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.techup.spring_demo.dto.NoteRequest;
import com.techup.spring_demo.dto.NoteResponse;

import com.techup.spring_demo.service.SupabaseStorageService;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor 
public class NoteService {

  private final NoteRepository noteRepository; // final = เหมือน const ใน JS

/** READ ALL: Entity -> Response DTO (list) */
  public List<NoteResponse> getAll() {
    return noteRepository.findAll()
        .stream()
        .map(this::toResponse)
        .toList();
  }

  /** READ ONE: not found -> 404 */
  public NoteResponse getById(Long id) {
    Note n = noteRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    return toResponse(n);
  }

  /** CREATE: Request DTO -> Entity -> save -> Response DTO */
  public NoteResponse create(NoteRequest req) {
    Note toSave = new Note();
    toSave.setTitle(req.getTitle());
    toSave.setContent(req.getContent());

    Note saved = noteRepository.save(toSave);
    return toResponse(saved);
  }

  /** UPDATE: find -> mutate -> save -> Response DTO */
  public NoteResponse update(Long id, NoteRequest req) {
    Note existing = noteRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));

    existing.setTitle(req.getTitle());
    existing.setContent(req.getContent());

    Note saved = noteRepository.save(existing);
    return toResponse(saved);
  }

  /** DELETE: not found -> 404, success -> void */
  public void delete(Long id) {
    Note existing = noteRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    noteRepository.delete(existing);
  }

  public NoteResponse attachFileUrl(Long id, String url) {
  Note note = noteRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));

    note.setImageUrl(url);
    Note saved = noteRepository.save(note);
    return toResponse(saved);
  }


  // ---------- mapper ภายในชั้น service ----------
    private NoteResponse toResponse(Note n) {
    return NoteResponse.builder()
        .id(n.getId())
        .title(n.getTitle())
        .content(n.getContent())
        .build();
  }

}