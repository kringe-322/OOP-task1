package ru.uni.orgapproval.document;

import ru.uni.orgapproval.model.Employee;

import java.time.LocalDate;
import java.util.Objects;

public class Document {
    private final String id;
    private final String title;
    private final Employee author;
    private final LocalDate creationDate;
    private DocumentStatus status;

    public Document(String id, String title, Employee author) {
        this.id = Objects.requireNonNull(id, "id не может быть null");
        this.title = Objects.requireNonNull(title, "title не может быть null");
        this.author = Objects.requireNonNull(author, "author не может быть null");

        this.creationDate = LocalDate.now();
        this.status = DocumentStatus.DRAFT;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public Employee getAuthor() {
        return author;
    }
    public LocalDate getCreationDate() {
        return creationDate;
    }
    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(final DocumentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if(obj == null || getClass() != obj.getClass()) return false;
      Document document = (Document) obj;
      return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Document{"+
                "id=" +'\''+id+ '\'' +
                ", title=" +'\''+title + '\''+
                ", author=" + author.getFullName() +
                ", creationDate=" + creationDate +
                ", status=" + status +
                '}';
    }
}
