import 'package:flutter/material.dart';
import 'package:frontend_mobile/auth/register.dart';
import 'package:frontend_mobile/auth/login.dart';
import 'package:frontend_mobile/widget/drawer.dart';
import 'package:carousel_slider/carousel_slider.dart';
import 'package:frontend_mobile/service/catalog_service.dart';
import 'package:frontend_mobile/catalog/all_product.dart';
import 'package:frontend_mobile/models/catalog.dart';
import 'dart:typed_data';
import 'package:intl/intl.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'My App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      initialRoute: '/',
      routes: {
        '/': (context) => MyHomePage(),
        '/register': (context) => RegisterPage(),
        '/login': (context) => LoginPage(),
      },
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key}) : super(key: key);

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late Future<List<Result>> latestProducts;

  @override
  void initState() {
    super.initState();
    CatalogService catalogService = CatalogService(
      baseUrl: 'https://apap-189.cs.ui.ac.id',
    );
    latestProducts = catalogService.getAllCatalogs();
  }

  Future<Uint8List> getImage(String catalogId) async {
    try {
      CatalogService catalogService = CatalogService(
        baseUrl: 'https://apap-189.cs.ui.ac.id',
      );
      return await catalogService.getImage(catalogId);
    } catch (e) {
      // Handle error
      return Uint8List(0);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Home'),
      ),
      drawer: const Drawers(),
      body: SingleChildScrollView(
        child: Stack(
          children: [
            // Background
            Positioned(
              top: 0,
              left: 0,
              child: Container(
                height: MediaQuery.of(context).size.height * 0.35,
                width: MediaQuery.of(context).size.width,
                decoration: const BoxDecoration(
                  color: Color(0xFF124B68),
                  borderRadius: BorderRadius.only(
                    bottomLeft: Radius.circular(20),
                    bottomRight: Radius.circular(20),
                  ),
                ),
              ),
            ),

            // Content
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  CarouselSlider(
                    options: CarouselOptions(
                      height: 200,
                      enlargeCenterPage: true,
                      autoPlay: true,
                      aspectRatio: 16 / 9,
                      autoPlayCurve: Curves.fastOutSlowIn,
                      enableInfiniteScroll: true,
                      autoPlayAnimationDuration: Duration(milliseconds: 800),
                      viewportFraction: 0.8,
                    ),
                    items: [
                      'assets/images/banner-1.png',
                      'assets/images/banner-2.png',
                      'assets/images/banner-3.png',
                    ].map((String imagePath) {
                      return Builder(
                        builder: (BuildContext context) {
                          return Container(
                            width: MediaQuery.of(context).size.width,
                            margin: EdgeInsets.symmetric(horizontal: 5.0),
                            child: Image.asset(
                              imagePath,
                              fit: BoxFit.cover,
                            ),
                          );
                        },
                      );
                    }).toList(),
                  ),

                  // "Latest Product" and "See More" section
                  Container(
                    margin: const EdgeInsets.only(
                        top: 30.0), // Adjust top margin as needed
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          'Latest Product',
                          style: TextStyle(
                            color: const Color(0xFF124B68),
                            fontWeight: FontWeight.bold,
                            fontSize: 14.0,
                          ),
                        ),
                        GestureDetector(
                          onTap: () async {
                            CatalogService catalogService = CatalogService(
                              baseUrl: 'http://apap-189.cs.ui.ac.id',
                            );
                            await Navigator.pushReplacement(
                              context,
                              MaterialPageRoute(
                                builder: (context) => CatalogListWidget(
                                  catalogService: catalogService,
                                ),
                              ),
                            );
                          },
                          child: const Text(
                            'See More',
                            style: TextStyle(
                              color: Colors.blue,
                              fontWeight: FontWeight.bold,
                              fontSize: 14.0,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  // Display Latest Products
                  FutureBuilder<List<Result>>(
                    future: latestProducts,
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.waiting) {
                        return CircularProgressIndicator();
                      }
                      if (snapshot.hasError) {
                        return Text('Error: ${snapshot.error}');
                      }
                      if (!snapshot.hasData || snapshot.data!.isEmpty) {
                        return Center(child: Text('No products found'));
                      }

                      return ListView.builder(
                        shrinkWrap: true,
                        itemCount: snapshot.data!.length,
                        itemBuilder: (context, index) {
                          var product = snapshot.data![index];
                          return Card(
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(8),
                              ),
                              clipBehavior: Clip.antiAliasWithSaveLayer,
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: <Widget>[
                                  FutureBuilder<Uint8List>(
                                    future: getImage(product.id),
                                    builder: (context, imageSnapshot) {
                                      if (imageSnapshot.connectionState ==
                                          ConnectionState.done) {
                                        if (imageSnapshot.hasData) {
                                          return Image.memory(
                                            imageSnapshot.data!,
                                            height: 160,
                                            width: MediaQuery.of(context)
                                                    .size
                                                    .width *
                                                0.4,
                                            fit: BoxFit.cover,
                                          );
                                        } else {
                                          return Text('Error loading image');
                                        }
                                      } else {
                                        return Center(
                                            child: CircularProgressIndicator());
                                      }
                                    },
                                  ),
                                  Container(
                                      padding: const EdgeInsets.fromLTRB(
                                          15, 15, 15, 15),
                                      child: Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                        children: <Widget>[
                                          Text(
                                            product.productName,
                                            style: TextStyle(
                                              fontWeight: FontWeight.bold,
                                              fontSize: 16,
                                              color: Colors.grey[800],
                                            ),
                                          ),
                                          Container(height: 10),
                                          Text(
                                            'Price: ${NumberFormat.currency(locale: 'en_US', symbol: '\$').format(product.price)}',
                                            style: TextStyle(
                                              color: Colors.blue,
                                              fontSize: 14,
                                            ),
                                          ),
                                        ],
                                      ))
                                ],
                              ));
                        },
                      );
                    },
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
      // Your footer section here
      bottomNavigationBar: BottomNavigationBar(
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.shopping_bag),
            label: 'Catalog',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.shopping_cart),
            label: 'Order History',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
        selectedItemColor:
            const Color(0xFF124B68), // Set the selected item color
        unselectedItemColor: Colors.grey, // Set the unselected item color
      ),
    );
  }
}
