#!/bin/bash
# Cleanup script - Remove extra documentation files

cd /home/ashish/Code/Java/Spring/Clubs

# Remove extra markdown files (keeping only README.md and APIs.md)
rm -f START_HERE.md
rm -f README_PHASE2.md
rm -f PHASE_2_GUIDE.md
rm -f PHASE_2_IMPLEMENTATION_COMPLETE.md
rm -f PHASE_2_STATUS.md
rm -f VERIFICATION_CHECKLIST.md
rm -f FINAL_SUMMARY.md
rm -f DELIVERABLES_LIST.md
rm -f DOCUMENTATION_INDEX.md
rm -f PHASE_2_IMPLEMENTATION.md
rm -f ACTION_ITEMS.md
rm -f READY_TO_USE.md
rm -f PHASE2_COMPLETE.md
rm -f IMPLEMENTATION_STATUS.md

echo "✅ Cleanup complete! Extra markdown files removed."
echo "✅ Keeping: README.md and APIs.md"

