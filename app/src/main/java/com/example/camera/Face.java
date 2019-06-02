package com.example.camera;



import java.io.Serializable;


import java.io.Serializable;



    public class Face implements Serializable, Comparable<Face> {
        private String id;
        private String age;
        private String gender;

        public Face(String id, String age, String gender) {
            this.id = id;
            this.age = age;
            this.gender = gender;
        }

        public Face() {
        }

        public String getId() {
            return id;
        }

        public String getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        @Override
        public String toString() {
            return "Face{" +
                    "id='" + id + '\'' +
                    ", age='" + age + '\'' +
                    ", gender='" + gender + '\'' +
                    '}';
        }

        @Override
        public int compareTo(Face o) {

            return id.compareTo(o.id);
        }
    }


