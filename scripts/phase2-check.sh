#!/bin/bash
# Phase 2 Build Verification Script

echo "================================"
echo "Phase 2 Implementation - Build Check"
echo "================================"
echo ""

cd /home/ashish/Code/Java/Spring/Clubs

echo "Step 1: Checking required files..."
FILES=(
  "services/club-service/src/main/java/com/ashish/clubs/services/clubservice/entity/ClubEntity.java"
  "services/club-service/src/main/java/com/ashish/clubs/services/clubservice/entity/MembershipEntity.java"
  "services/club-service/src/main/java/com/ashish/clubs/services/clubservice/repository/ClubRepository.java"
  "services/club-service/src/main/java/com/ashish/clubs/services/clubservice/repository/MembershipRepository.java"
  "services/club-service/src/main/java/com/ashish/clubs/services/clubservice/service/ClubService.java"
  "services/club-service/src/main/java/com/ashish/clubs/services/clubservice/controller/ClubController.java"
  "services/feed-service/src/main/java/com/ashish/clubs/services/feedservice/entity/PostDocument.java"
  "services/feed-service/src/main/java/com/ashish/clubs/services/feedservice/service/PostService.java"
  "services/feed-service/src/main/java/com/ashish/clubs/services/feedservice/controller/PostController.java"
  "common/common-messaging/src/main/java/com/ashish/clubs/common/messaging/event/DomainEvent.java"
  "common/common-messaging/src/main/java/com/ashish/clubs/common/messaging/producer/KafkaEventProducer.java"
)

for file in "${FILES[@]}"; do
  if [ -f "$file" ]; then
    echo "✓ $file"
  else
    echo "✗ MISSING: $file"
  fi
done

echo ""
echo "Step 2: Running Maven compilation..."
mvn clean compile -DskipTests -q

if [ $? -eq 0 ]; then
  echo "✓ Compilation successful!"
  echo ""
  echo "Step 3: Build packages..."
  mvn package -DskipTests -q -rf :club-service
  if [ $? -eq 0 ]; then
    echo "✓ Package build successful!"
    echo "✓ Phase 2 implementation is ready for deployment!"
  else
    echo "✗ Package build failed. Check errors above."
  fi
else
  echo "✗ Compilation failed. Check errors above."
fi

echo ""
echo "================================"
echo "Build Check Complete"
echo "================================"

