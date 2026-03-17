### SE333 Assignment 5

[![SE333_CI](https://github.com/Alexis-Aguilar22/Assignment5_code/actions/workflows/SE333_CI.yml/badge.svg)](https://github.com/Alexis-Aguilar22/Assignment5_code/actions/workflows/SE333_CI.yml)

### Project Overview

This project completes both parts of Assignment 5 for SE333.

Part 1 focuses on testing the `Amazon` package using:
- unit tests with Mockito
- integration tests using the in-memory HSQLDB database
- GitHub Actions CI
- Checkstyle static analysis
- JaCoCo code coverage reporting

Part 2 focuses on user-interface testing using Playwright for the DePaul bookstore purchase pathway, including:
- a traditional Playwright test package
- an LLM-generated style Playwright test package
- GitHub Actions automation for UI test execution
- recorded test videos saved during test runs

### Project Structure

- `src/main/java/org/example/Amazon`  
  Main Amazon source code

- `src/test/java/org/example/Amazon/AmazonUnitTest.java`  
  Unit tests using mocks

- `src/test/java/org/example/Amazon/AmazonIntegrationTest.java`  
  Integration tests using the database-backed shopping cart

- `src/test/java/playwrightTraditional/BookstoreTraditionalTest.java`  
  Traditional Playwright UI tests

- `src/test/java/playwrightLLM/BookstoreLLMTest.java`  
  LLM-generated style Playwright UI tests

- `.github/workflows/SE333_CI.yml`  
  GitHub Actions workflow

- `reflection.md`  
  Reflection comparing manual UI testing and AI-assisted UI testing

### What the Workflow Does

On every push to the `main` branch, GitHub Actions will:
1. build the Maven project
2. run Checkstyle during the `validate` phase
3. upload the `checkstyle.xml` artifact
4. install Playwright Chromium dependencies
5. run all JUnit and Playwright tests
6. generate JaCoCo coverage
7. upload the `jacoco.xml` artifact
8. upload Playwright video recordings

### Reports and Artifacts

The workflow produces:
- `target/checkstyle.xml`
- `target/site/jacoco/jacoco.xml`
- `videos/` recordings from Playwright runs

### Confirmation

This repository is configured so that:
- static analysis runs before testing
- tests run automatically on pushes to `main`
- coverage is generated with JaCoCo
- Checkstyle and JaCoCo artifacts are uploaded by GitHub Actions
- a workflow badge is displayed in this README

### Running Locally

### Amazon tests
Run:
```bash
mvn test

Demo link:
https://drive.google.com/file/d/1t3dFwz9W5xrtnwsEMfmBtA5hOtnYQEEe/view?usp=sharing
