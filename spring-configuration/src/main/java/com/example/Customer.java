package com.example;

public class Customer {

 private Long id;

 private String email;

 public Customer(Long id, String email) {
  this.id = id;
  this.email = email;
 }

 @Override
 public String toString() {
  return "Customer{" + "id=" + id + ", email='" + email + '\'' + '}';
 }

 public Long getId() {
  return id;
 }

 public String getEmail() {
  return email;
 }
}
