--liquibase formatted sql

--changeset FDKost:1
INSERT INTO client(id, name, open_key)
VALUES ('69bff558-24bc-4cf8-a479-ff99a61cd28b', 'Tom', '-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqoGRIo5jDNpAsn/oPb+j
4IeiAta6vyFRNAFDufNi0+vH3w2mZbFMzjrgM49bc3rBOglo/zyRAHieIELmp8Cu
KJ9gzhrHgf2+oJmdEtwxEEuQqbJJ3PV6tYoBHgrjGAHVCOUndjYSojMIPTM3gv2F
a3AYWaat+BXIbS2Ax5LMK9LRUFqoZJyMvpMdOL2RWSim/XBIosyYITy2Azv1zm8Q
HjHmbUkhgyHQr3oJxiLfSNXaKdad2nKsRKVUNwWgalythusuDkrMBuL8hgWRdm7T
gHn3MjkLouypyuTXI37iCsLpVPdqjaYy//13XDNZghcvT0jSqEKMnNzO4FSvzEh7
FwIDAQAB
-----END PUBLIC KEY-----'),
       (uuid_generate_v4(), 'Bob', '-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqoGRIo5jDNpAsn/oPb+j
4IeiAta6vyFRNAFDufNi0+vH3w2mZbFMzjrgM49bc3rBOglo/zyRAHieIELmp8Cu
KJ9gzhrHgf2+oJmdEtwxEEuQqbJJ3PV6tYoBHgrjGAHVCOUndjYSojMIPTM3gv2F
a3AYWaat+BXIbS2Ax5LMK9LRUFqoZJyMvpMdOL2RWSim/XBIosyYITy2Azv1zm8Q
HjHmbUkhgyHQr3oJxiLfSNXaKdad2nKsRKVUNwWgalythusuDkrMBuL8hgWRdm7T
gHn3MjkLouypyuTXI37iCsLpVPdqjaYy//13XDNZghcvT0jSqEKMnNzO4FSvzEh7
FwIDAQAB
-----END PUBLIC KEY-----');
