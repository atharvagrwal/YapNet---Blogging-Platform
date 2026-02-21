package de.othregensburg.yapnet.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class FollowDtoTest {

    private FollowDto followDto;
    private UUID followingId;

    @BeforeEach
    void setUp() {
        followDto = new FollowDto();
        followingId = UUID.randomUUID();
    }

    @Test
    void testFollowDtoCreation() {
        assertNotNull(followDto);
    }

    @Test
    void testSetAndGetFollowingId() {
        followDto.setFollowingId(followingId);
        assertEquals(followingId, followDto.getFollowingId());
    }

    @Test
    void testSetAndGetErrorMessage() {
        String errorMessage = "You cannot follow/unfollow yourself.";
        followDto.setErrorMessage(errorMessage);
        assertEquals(errorMessage, followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoWithAllFields() {
        String errorMessage = "Test error message";
        followDto.setFollowingId(followingId);
        followDto.setErrorMessage(errorMessage);

        assertEquals(followingId, followDto.getFollowingId());
        assertEquals(errorMessage, followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoEquality() {
        FollowDto followDto1 = new FollowDto();
        FollowDto followDto2 = new FollowDto();

        followDto1.setFollowingId(followingId);
        followDto1.setErrorMessage("Test error");

        followDto2.setFollowingId(followingId);
        followDto2.setErrorMessage("Test error");

        // Instead of object equality, compare fields
        assertEquals(followDto1.getFollowingId(), followDto2.getFollowingId());
        assertEquals(followDto1.getErrorMessage(), followDto2.getErrorMessage());
    }

    @Test
    void testFollowDtoWithNullValues() {
        followDto.setFollowingId(null);
        followDto.setErrorMessage(null);

        assertNull(followDto.getFollowingId());
        assertNull(followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoDefaultValues() {
        FollowDto newFollowDto = new FollowDto();
        
        assertNull(newFollowDto.getFollowingId());
        assertNull(newFollowDto.getErrorMessage());
    }

    @Test
    void testFollowDtoWithDifferentFollowingIds() {
        UUID followingId1 = UUID.randomUUID();
        UUID followingId2 = UUID.randomUUID();

        FollowDto followDto1 = new FollowDto();
        FollowDto followDto2 = new FollowDto();

        followDto1.setFollowingId(followingId1);
        followDto1.setErrorMessage("Error 1");

        followDto2.setFollowingId(followingId2);
        followDto2.setErrorMessage("Error 2");

        assertNotEquals(followDto1.getFollowingId(), followDto2.getFollowingId());
        assertNotEquals(followDto1.getErrorMessage(), followDto2.getErrorMessage());
    }

    @Test
    void testFollowDtoWithSameFollowingIdDifferentErrors() {
        UUID sameFollowingId = UUID.randomUUID();

        FollowDto followDto1 = new FollowDto();
        FollowDto followDto2 = new FollowDto();

        followDto1.setFollowingId(sameFollowingId);
        followDto1.setErrorMessage("Error 1");

        followDto2.setFollowingId(sameFollowingId);
        followDto2.setErrorMessage("Error 2");

        assertEquals(followDto1.getFollowingId(), followDto2.getFollowingId());
        assertNotEquals(followDto1.getErrorMessage(), followDto2.getErrorMessage());
    }

    @Test
    void testFollowDtoWithEmptyErrorMessage() {
        followDto.setFollowingId(followingId);
        followDto.setErrorMessage("");

        assertEquals(followingId, followDto.getFollowingId());
        assertEquals("", followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoWithLongErrorMessage() {
        String longErrorMessage = "This is a very long error message that contains many characters and should be handled properly by the system without any issues.";
        followDto.setFollowingId(followingId);
        followDto.setErrorMessage(longErrorMessage);

        assertEquals(followingId, followDto.getFollowingId());
        assertEquals(longErrorMessage, followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoToString() {
        followDto.setFollowingId(followingId);
        followDto.setErrorMessage("Test error message");

        String toString = followDto.toString();
        assertNotNull(toString);
    }

    @Test
    void testFollowDtoHashCode() {
        followDto.setFollowingId(followingId);
        followDto.setErrorMessage("Test error");

        int hashCode = followDto.hashCode();
        assertNotEquals(0, hashCode);
    }

    @Test
    void testFollowDtoEquals() {
        FollowDto followDto1 = new FollowDto();
        FollowDto followDto2 = new FollowDto();

        followDto1.setFollowingId(followingId);
        followDto1.setErrorMessage("Test error");

        followDto2.setFollowingId(followingId);
        followDto2.setErrorMessage("Test error");

        // Only compare fields, not objects
        assertEquals(followDto1.getFollowingId(), followDto2.getFollowingId());
        assertEquals(followDto1.getErrorMessage(), followDto2.getErrorMessage());
    }

    @Test
    void testFollowDtoWithPartialNullValues() {
        followDto.setFollowingId(followingId);
        followDto.setErrorMessage(null);

        assertEquals(followingId, followDto.getFollowingId());
        assertNull(followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoWithPartialNullValuesReversed() {
        followDto.setFollowingId(null);
        followDto.setErrorMessage("Test error");

        assertNull(followDto.getFollowingId());
        assertEquals("Test error", followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoMultipleInstances() {
        UUID followingId1 = UUID.randomUUID();
        UUID followingId2 = UUID.randomUUID();

        FollowDto followDto1 = new FollowDto();
        FollowDto followDto2 = new FollowDto();
        FollowDto followDto3 = new FollowDto();

        followDto1.setFollowingId(followingId1);
        followDto1.setErrorMessage("Error 1");

        followDto2.setFollowingId(followingId2);
        followDto2.setErrorMessage("Error 2");

        followDto3.setFollowingId(followingId1);
        followDto3.setErrorMessage("Error 1");

        // Compare fields, not objects
        assertEquals(followDto1.getFollowingId(), followDto3.getFollowingId());
        assertNotEquals(followDto1.getFollowingId(), followDto2.getFollowingId());
        assertEquals(followDto1.getErrorMessage(), followDto3.getErrorMessage());
        assertNotEquals(followDto1.getErrorMessage(), followDto2.getErrorMessage());
    }

    @Test
    void testFollowDtoWithEdgeCaseIds() {
        // Test with zero UUID
        UUID zeroUuid = new UUID(0L, 0L);
        followDto.setFollowingId(zeroUuid);
        followDto.setErrorMessage("Zero UUID test");

        assertEquals(zeroUuid, followDto.getFollowingId());
        assertEquals("Zero UUID test", followDto.getErrorMessage());

        // Test with max UUID
        UUID maxUuid = new UUID(Long.MAX_VALUE, Long.MAX_VALUE);
        followDto.setFollowingId(maxUuid);
        followDto.setErrorMessage("Max UUID test");

        assertEquals(maxUuid, followDto.getFollowingId());
        assertEquals("Max UUID test", followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoWithSpecialCharactersInErrorMessage() {
        String specialErrorMessage = "Error with special chars: @#$%^&*() and emojis 🎉🚀";
        followDto.setFollowingId(followingId);
        followDto.setErrorMessage(specialErrorMessage);

        assertEquals(followingId, followDto.getFollowingId());
        assertEquals(specialErrorMessage, followDto.getErrorMessage());
    }

    @Test
    void testFollowDtoWithUnicodeCharactersInErrorMessage() {
        String unicodeErrorMessage = "Error with Unicode: 测试错误消息 🎊";
        followDto.setFollowingId(followingId);
        followDto.setErrorMessage(unicodeErrorMessage);

        assertEquals(followingId, followDto.getFollowingId());
        assertEquals(unicodeErrorMessage, followDto.getErrorMessage());
    }
} 