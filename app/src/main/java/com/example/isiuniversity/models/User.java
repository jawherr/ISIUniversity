package com.example.isiuniversity.models;

public class User {
   private String id, firstName, lastname, email;

   public User() {
   }

   public User(String id, String firstName, String lastname, String email) {
      this.id = id;
      this.firstName = firstName;
      this.lastname = lastname;
      this.email = email;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastname() {
      return lastname;
   }

   public void setLastname(String lastname) {
      this.lastname = lastname;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }
}
