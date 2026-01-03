# Unify - College Community & Marketplace App

A comprehensive Android social platform designed for college students to connect through community hubs and trade items via an integrated marketplace. Employed MVVM architecture development pattern.

## Overview

Unify is a dual-purpose mobile application that combines social networking with e-commerce functionality. Students can join community hubs based on their interests, participate in threaded discussions, and buy/sell items within their campus community—all in one unified platform.

## Key Features

### Community Hubs
- Create and join interest-based communities
- Browse hubs by category (Academic, Social, Sports, Arts, etc.)
- Hub creators have moderation capabilities and can manage members

### Threaded Discussions
- Share posts with rich text and images
- Full-featured comment system with nested replies
- Real-time updates across all users
- Save posts to "Saved Stuff" for easy access

### Integrated Marketplace
- Special post type for buying/selling items with pricing and contact info
- Filter marketplace items by category (Electronics, Textbooks, Furniture, etc.)
- Shopping cart for easy item tracking
- Support for product images

### User Features
- Secure Firebase authentication
- Profile management for posts, hubs, and saved content
- Recent Activity tracking showing posts you've commented on and new replies to your comments
- University affiliation display

## Technical Architecture

### Frontend
- Language: Kotlin
- UI Framework: Jetpack Compose (declarative UI)
- Navigation: Jetpack Navigation Component
- State Management: MutableState with remember/LaunchedEffect
- Material Design: Material3 components

### Backend & Services
- Database: Cloud Firestore (NoSQL)
- Authentication: Firebase Authentication
- Storage: Firebase Storage
- Real-time Sync: Firestore real-time listeners

### Data Models
- Posts: Unified model supporting both regular and market posts with isMarketPost flag
- Comments: Hierarchical structure with parentCommentId for nested replies
- Users: Profiles with authentication info and university affiliation
- Hubs: Community entities with creator info and member lists

## Technical Highlights

- Complete Firebase integration with real-time listeners and CRUD operations
- Smart post system using single model for both regular and market posts
- Intelligent comment tracking that filters direct replies to user comments
- Separate storage systems for cart items and saved posts
- Base64 image encoding for inline storage and display
- Proper lifecycle management with LaunchedEffect
- Type-safe Kotlin with null safety

## Setup & Installation

1. Clone the repository

2. Open in Android Studio (Hedgehog or later)

3. Add Firebase configuration:
   - Create a Firebase project at console.firebase.google.com
   - Download google-services.json
   - Place in app/ directory

4. Enable Firebase services:
   - Authentication (Email/Password)
   - Cloud Firestore
   - Firebase Storage

5. Build and run on emulator or physical device

## Future Enhancements

- Direct messaging between users
- Push notifications for replies and marketplace activity
- Camera/gallery image uploads
- Advanced search with filters
- User reputation system
- In-app transactions
- Hub analytics
- Dark mode support

## Technologies

- Kotlin
- Jetpack Compose
- Firebase (Firestore, Auth, Storage)
- Material Design 3
- Android SDK 34
