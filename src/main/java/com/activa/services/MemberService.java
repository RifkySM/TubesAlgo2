package com.activa.services;

import com.activa.models.Member;
import com.activa.repositories.MemberRepository;
import com.activa.utils.Helper; // Import Helper class
import javafx.scene.control.Alert.AlertType; // Import AlertType

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Menangani logika bisnis dan validasi untuk entitas Member.
 * Terintegrasi dengan Helper untuk menampilkan dialog.
 */
public class MemberService {

    private final MemberRepository memberRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public MemberService() {
        this.memberRepository = new MemberRepository();
    }

    /**
     * Memvalidasi apakah sebuah string null atau kosong.
     * @param value String yang akan divalidasi.
     * @param fieldName Nama field untuk pesan error.
     * @throws IllegalArgumentException jika string tidak valid.
     */
    private void validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " tidak boleh kosong.");
        }
    }

    /**
     * Memvalidasi format email.
     * @param email Email yang akan divalidasi.
     * @throws IllegalArgumentException jika format email tidak valid.
     */
    private void validateEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Format email tidak valid.");
        }
    }

    /**
     * Membuat member baru dengan validasi. Menampilkan dialog jika terjadi error.
     *
     * @return Optional yang berisi Member jika berhasil, jika tidak maka kosong.
     */
    public Optional<Member> createMember(String nim, String name, String email, LocalDate birthdate, String address) {
        try {
            validateString(nim, "NIM");
            validateString(name, "Nama");
            validateString(email, "Email");
            validateEmailFormat(email);
            validateString(address, "Alamat");
            if (birthdate == null) {
                throw new IllegalArgumentException("Tanggal lahir tidak boleh kosong.");
            }

            // Validasi duplikasi (asumsi MemberRepository memiliki metode ini)
             if (memberRepository.findByNim(nim).isPresent()) {
                 throw new IllegalStateException("NIM " + nim + " sudah terdaftar.");
             }
             if (memberRepository.findByEmail(email).isPresent()) {
                 throw new IllegalStateException("Email " + email + " sudah terdaftar.");
             }

            // Jika validasi lolos, buat member baru
            Member newMember = new Member(nim, name, email, birthdate, address);
            memberRepository.create(newMember);
            Helper.showAlert("Sukses", "Member baru berhasil dibuat.", AlertType.INFORMATION);
            return Optional.of(newMember);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Helper.showAlert("Gagal Membuat Member", e.getMessage(), AlertType.ERROR);
            return Optional.empty();
        }
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findMemberById(UUID memberId) {
        return memberRepository.findById(memberId);
    }

    /**
     * Memperbarui detail profil dari member yang sudah ada. Menampilkan dialog jika terjadi error.
     *
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateMemberDetails(UUID memberId, String newNim, String newName, String newEmail, LocalDate newBirthdate, String newAddress) {
        try {
            // Validasi input dasar
            validateString(newNim, "NIM");
            validateString(newName, "Nama");
            validateString(newEmail, "Email");
            validateEmailFormat(newEmail);
            validateString(newAddress, "Alamat");
            if (newBirthdate == null) {
                throw new IllegalArgumentException("Tanggal lahir tidak boleh kosong.");
            }

            // Pastikan member ada
            Member existingMember = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalStateException("Member dengan ID " + memberId + " tidak ditemukan."));

            // Validasi duplikasi (jika NIM/email diubah)
             Optional<Member> memberWithSameNim = memberRepository.findByNim(newNim);
             if (memberWithSameNim.isPresent() && !memberWithSameNim.get().getId().equals(memberId)) {
                 throw new IllegalStateException("NIM " + newNim + " sudah digunakan oleh member lain.");
             }
             Optional<Member> memberWithSameEmail = memberRepository.findByEmail(newEmail);
             if (memberWithSameEmail.isPresent() && !memberWithSameEmail.get().getId().equals(memberId)) {
                 throw new IllegalStateException("Email " + newEmail + " sudah digunakan oleh member lain.");
             }

            // Update detail member
            existingMember.setNim(newNim);
            existingMember.setName(newName);
            existingMember.setEmail(newEmail);
            existingMember.setBirthdate(newBirthdate);
            existingMember.setAddress(newAddress);

            memberRepository.update(existingMember);
            Helper.showAlert("Sukses", "Detail member berhasil diperbarui.", AlertType.INFORMATION);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            Helper.showAlert("Gagal Memperbarui Member", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    /**
     * Menonaktifkan akun member. Menampilkan dialog jika terjadi error.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean deactivateMember(UUID memberId) {
        try {
            if (memberRepository.findById(memberId).isEmpty()) {
                throw new IllegalStateException("Member dengan ID " + memberId + " tidak ditemukan.");
            }
            memberRepository.updateStatus(memberId, false);
            Helper.showAlert("Sukses", "Member berhasil dinonaktifkan.", AlertType.INFORMATION);
            return true;
        } catch (IllegalStateException e) {
            Helper.showAlert("Operasi Gagal", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    /**
     * Mengaktifkan kembali akun member. Menampilkan dialog jika terjadi error.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean reactivateMember(UUID memberId) {
        try {
            if (memberRepository.findById(memberId).isEmpty()) {
                throw new IllegalStateException("Member dengan ID " + memberId + " tidak ditemukan.");
            }
            memberRepository.updateStatus(memberId, true);
            Helper.showAlert("Sukses", "Member berhasil diaktifkan kembali.", AlertType.INFORMATION);
            return true;
        } catch (IllegalStateException e) {
            Helper.showAlert("Operasi Gagal", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    /**
     * Melakukan soft-delete pada member dari sistem. Menampilkan dialog jika terjadi error.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean deleteMember(UUID memberId) {
        try {
            if (memberRepository.findById(memberId).isEmpty()) {
                throw new IllegalStateException("Member dengan ID " + memberId + " tidak ditemukan.");
            }
            memberRepository.softDeleteById(memberId);
            Helper.showAlert("Sukses", "Member berhasil dihapus.", AlertType.INFORMATION);
            return true;
        } catch (IllegalStateException e) {
            Helper.showAlert("Operasi Gagal", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }
}
