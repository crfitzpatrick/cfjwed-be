/*
 * MIT License
 *
 * Copyright (c) 2016  Christopher R. Fitzpatrick
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cfitzarl.cfjwed.controller;

import com.cfitzarl.cfjwed.core.security.SecurityContextWrapper;
import com.cfitzarl.cfjwed.data.dto.AttendantDTO;
import com.cfitzarl.cfjwed.data.dto.InvitationDTO;
import com.cfitzarl.cfjwed.data.model.Attendant;
import com.cfitzarl.cfjwed.data.model.Invitation;
import com.cfitzarl.cfjwed.exception.ResourceNotFoundException;
import com.cfitzarl.cfjwed.service.AttendantService;
import com.cfitzarl.cfjwed.service.InvitationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * This provides all API functionality related to {@link Invitation}s.
 */
@Controller
@ResponseBody
@RequestMapping("/api/invitations")
public class InvitationController {

    @Autowired
    private AttendantService attendantService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * This returns a list of {@link Invitation} data by transforming them into a list of {@link InvitationDTO}s.
     *
     * @return the list of invitation DTOs
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<InvitationDTO> provideInvitations() {
        List<InvitationDTO> invitations = new ArrayList<>();

        for (Invitation invitation : invitationService.find()) {
            invitations.add(modelMapper.map(invitation, InvitationDTO.class));
        }

        return invitations;
    }

    /**
     * This returns information for a particular {@link Invitation} keyed by its primary key.
     *
     * @param id the ID of the invitation
     * @return invitation data
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public InvitationDTO provideInvitation(@PathVariable Long id) {
        Invitation invitation = invitationService.find(id);

        if (invitation == null) { throw new ResourceNotFoundException(); }
        SecurityContextWrapper.authorize(invitation.getAccount());

        return modelMapper.map(invitation, InvitationDTO.class);
    }

    /**
     * This deletes an invitation by its primary key.
     *
     * @param id the ID of the invitation
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteInvitation(@PathVariable Long id) {
        invitationService.delete(id);
    }

    /**
     * This returns a list of {@link Attendant}s as represented by {@link AttendantDTO}s that belong
     * to an {@link Invitation}.
     *
     * @param id the ID of the invitation
     * @return a list of attendant data
     */
    @RequestMapping(value = "/{id}/attendants", method = RequestMethod.GET)
    public List<AttendantDTO> returnAttendants(@PathVariable Long id) {
        List<AttendantDTO> attendants = new ArrayList<>();

        for (Attendant attendant : attendantService.findByInvitation(id)) {
            attendants.add(modelMapper.map(attendant, AttendantDTO.class));
        }

        return attendants;
    }

    /**
     * This creates an {@link Attendant}.
     *
     * @param invitationId The ID of the {@link Invitation} the {@link Attendant} will belong to. This is present to
     *                     conform with REST principles
     * @param attendantDTO the attendant data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{invitationId}/attendants", method = RequestMethod.POST)
    public void processAttendant(@PathVariable Long invitationId, @Valid @RequestBody AttendantDTO attendantDTO) {
        attendantService.save(modelMapper.map(attendantDTO, Attendant.class));
    }

    /**
     * This deletes an {@link Attendant}.
     *
     * @param invitationId The ID of the {@link Invitation} the {@link Attendant} belongs to. This is present to
     *                     conform with REST principles
     * @param attendantId The ID of the {@link Attendant}
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{invitationId}/attendants/{attendantId}", method = RequestMethod.DELETE)
    public void deleteAttendant(@PathVariable Long invitationId, @PathVariable Long attendantId) {
        attendantService.delete(attendantId);
    }

    /**
     * This creates an {@link Invitation}.
     *
     * @param invitationDTO the invitation data
     * @return the saved invitation data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public InvitationDTO createInvitation(@Valid @RequestBody InvitationDTO invitationDTO) {
        Invitation invitation = invitationService.upsert(modelMapper.map(invitationDTO, Invitation.class));
        return modelMapper.map(invitation, InvitationDTO.class);
    }
}
