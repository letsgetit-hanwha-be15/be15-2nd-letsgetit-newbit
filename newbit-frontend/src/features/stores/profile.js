import { defineStore } from "pinia";
import { ref } from "vue";
import { getUserProfile } from "@/api/user";

export const useProfileStore = defineStore("profile", () => {
  const profileCache = ref({});
  const isLoading = ref({});
  const CACHE_TTL = 1000 * 60 * 10;
  const lastFetchTime = ref({});

  function getDefaultProfileImage() {
    return "/src/assets/image/profile.png";
  }

  async function fetchUserProfile(userId, force = false) {
    if (!userId) return null;

    const numUserId = parseInt(userId);

    const now = Date.now();
    const hasCachedProfile = profileCache.value[numUserId];
    const cacheIsValid =
      lastFetchTime.value[numUserId] &&
      now - lastFetchTime.value[numUserId] < CACHE_TTL;

    if (hasCachedProfile && cacheIsValid && !force) {
      console.log(`[ProfileStore] Using cached profile for user ${numUserId}`);
      return profileCache.value[numUserId];
    }

    if (isLoading.value[numUserId]) {
      console.log(
        `[ProfileStore] Profile fetch already in progress for user ${numUserId}`
      );
      return new Promise((resolve) => {
        let attempts = 0;
        const checkInterval = setInterval(() => {
          if (profileCache.value[numUserId] || attempts >= 10) {
            clearInterval(checkInterval);
            resolve(profileCache.value[numUserId] || null);
          }
          attempts++;
        }, 500);
      });
    }

    try {
      isLoading.value[numUserId] = true;
      console.log(`[ProfileStore] Fetching profile for user ${numUserId}`);

      const response = await getUserProfile(numUserId);

      const profileData = response.data.data;

      profileCache.value[numUserId] = profileData;
      lastFetchTime.value[numUserId] = Date.now();

      console.log(
        `[ProfileStore] Profile fetched successfully for user ${numUserId}:`,
        profileData
      );
      return profileData;
    } catch (error) {
      console.error(
        `[ProfileStore] Failed to fetch profile for user ${numUserId}:`,
        error
      );
      return null;
    } finally {
      isLoading.value[numUserId] = false;
    }
  }

  function getCachedProfile(userId) {
    if (!userId) return null;
    const numUserId = parseInt(userId);
    return profileCache.value[numUserId] || null;
  }

  function getProfileImageUrl(profileImageUrl) {
    return profileImageUrl || getDefaultProfileImage();
  }

  function clearCache() {
    profileCache.value = {};
    isLoading.value = {};
    lastFetchTime.value = {};
  }

  return {
    fetchUserProfile,
    getCachedProfile,
    clearCache,
    profileCache,
    getDefaultProfileImage,
    getProfileImageUrl,
  };
});
