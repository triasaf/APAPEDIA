import 'package:flutter/material.dart';
import 'package:frontend_mobile/models/profile_response.dart';
import 'package:frontend_mobile/service/profile_service.dart';
import 'package:frontend_mobile/widget/drawer.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key, required this.profileService});

  final ProfileService profileService;

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  late Future<ProfileResponse> profileResponse;

  @override
  void initState() {
    super.initState();
    profileResponse = widget.profileService.getProfile(context);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: const Color(0x00124b68),
        drawer: const Drawers(),
        body: FutureBuilder<ProfileResponse>(
            future: profileResponse,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const Center(child: CircularProgressIndicator());
              } else if (snapshot.hasError) {
                return Center(child: Text('Error: ${snapshot.error}'));
              } else if (!snapshot.hasData) {
                return const Center(child: Text('No data available'));
              }

              var profileResponse = snapshot.data!;

              return Padding(
                  padding: const EdgeInsets.all(
                      16.0), // Atur padding sesuai kebutuhan
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(12.0),
                                boxShadow: const [
                                  BoxShadow(
                                    color: Colors.grey, // Warna bayangan
                                    blurRadius: 3.0, // Radius blur bayangan
                                    offset:
                                        Offset(0, 1), // Geser bayangan (x, y)
                                  ),
                                ],
                                color: Colors.white, // Warna latar belakang
                              ),
                              child: Padding(
                                  padding: const EdgeInsets.all(16.0),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Row(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.center,
                                        children: [
                                          Image.asset(
                                            "assets/images/avatar.png",
                                            width: 50,
                                          ),
                                          const SizedBox(width: 10),
                                          Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            children: [
                                              Text(
                                                profileResponse.profile.name,
                                                style: const TextStyle(
                                                    fontWeight: FontWeight.bold,
                                                    fontSize: 20),
                                              ),
                                              Text(profileResponse
                                                  .profile.username)
                                            ],
                                          )
                                        ],
                                      ),
                                      const SizedBox(height: 20),
                                      Container(
                                          decoration: BoxDecoration(
                                            borderRadius:
                                                BorderRadius.circular(4.0),
                                            color: Colors
                                                .lightBlue, // Warna latar belakang
                                          ),
                                          child: Padding(
                                            padding: const EdgeInsets.all(10),
                                            child: Row(
                                              mainAxisAlignment:
                                                  MainAxisAlignment
                                                      .spaceBetween,
                                              children: [
                                                Row(
                                                  crossAxisAlignment:
                                                      CrossAxisAlignment.start,
                                                  children: [
                                                    const Icon(
                                                      Icons
                                                          .account_balance_wallet, // Ganti dengan ikon yang sesuai
                                                      color: Colors.white,
                                                    ),
                                                    const SizedBox(
                                                      width: 5,
                                                    ),
                                                    Text(
                                                      "Rp ${profileResponse.profile.balance}",
                                                      style: const TextStyle(
                                                          color: Colors.white,
                                                          fontWeight:
                                                              FontWeight.bold),
                                                    ),
                                                  ],
                                                ),
                                                InkWell(
                                                  onTap: () {
                                                    // Navigator.push(
                                                    //   context,
                                                    //   MaterialPageRoute(builder: (context) => HalamanBaru()),
                                                    // );
                                                  },
                                                  child: const Icon(
                                                    Icons.add_circle_outline,
                                                    color: Colors.white,
                                                  ),
                                                )
                                              ],
                                            ),
                                          )),
                                    ],
                                  ))),
                          const SizedBox(
                            height: 15,
                          ),
                          Padding(
                            padding: const EdgeInsets.only(
                                left: 5, top: 0, right: 5, bottom: 0),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const SizedBox(
                                  height: 10,
                                ),
                                const Text(
                                  "Email",
                                  style: TextStyle(fontWeight: FontWeight.bold),
                                ),
                                Text(profileResponse.profile.email),
                                const SizedBox(
                                  height: 10,
                                ),
                                const Text(
                                  "Address",
                                  style: TextStyle(fontWeight: FontWeight.bold),
                                ),
                                Text(profileResponse.profile.address),
                              ],
                            ),
                          )
                        ],
                      ),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          InkWell(
                            onTap: () {
                              // Tambahkan logika untuk sign out di sini
                            },
                            child: const Row(
                              children: [
                                Icon(
                                  Icons.no_accounts,
                                  color: Colors.red,
                                ),
                                SizedBox(
                                    width: 8), // Jarak antara ikon dan teks
                                Text(
                                  "Delete Account",
                                  style: TextStyle(
                                    color: Colors.red,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          const Divider(
                            height: 20,
                            color: Colors.grey,
                          ),
                          InkWell(
                            onTap: () {
                              // Tambahkan logika untuk sign out di sini
                            },
                            child: const Row(
                              children: [
                                Icon(
                                  Icons.exit_to_app,
                                  color: Colors.red,
                                ),
                                SizedBox(
                                    width: 8), // Jarak antara ikon dan teks
                                Text(
                                  "Sign Out",
                                  style: TextStyle(
                                    color: Colors.red,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      )
                    ],
                  ));
            }));
  }
}
