package edu.buet.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.buet.data.Club;

class ClubListResponse extends MessageBase<List<ClubEntry>> {
    ClubListResponse(boolean success, String message, List<ClubEntry> value) {
        super(success, message, value);
    }
    static ClubListResponse errorMessage(String reason){
        return new ClubListResponse(false, reason, null);
    }
    static ClubListResponse successMessage(Collection<Club> clubs){
        var r = new ArrayList<ClubEntry>(clubs.size()); 
        for(var club : clubs)
            r.add(new ClubEntry(club.getId(), club.getName(), club.getAltName()));
        return new ClubListResponse(true, null, r);
    }
}
